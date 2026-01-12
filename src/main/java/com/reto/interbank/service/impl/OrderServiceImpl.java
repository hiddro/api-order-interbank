package com.reto.interbank.service.impl;

import com.reto.interbank.builder.OrderBuilder;
import com.reto.interbank.models.dto.ProductDto;
import com.reto.interbank.models.entities.Order;
import com.reto.interbank.repository.GenericRepo;
import com.reto.interbank.repository.OrderRepositories;
import com.reto.interbank.repository.ProductRepositories;
import com.reto.interbank.service.OrderService;
import com.reto.reto.interbank.dto.OrderRequest;
import com.reto.reto.interbank.dto.OrderResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Service
@Slf4j
public class OrderServiceImpl extends CrudServiceImpl<Order, Long> implements OrderService {

    @Autowired
    OrderRepositories orderRepositories;

    @Autowired
    ProductRepositories productRepositories;

    @Autowired
    OrderBuilder orderBuilder;

    @Override
    protected GenericRepo<Order, Long> getRepo() {
        return orderRepositories;
    }

    @Override
    public Mono<OrderResponse> getById(String id) {
        return null;
    }

    @Override
    public Mono<OrderResponse> registerOrder(OrderRequest orderRequest) {

        return Flux.fromIterable(orderRequest.getProductKeywords().getKeywords())
                .flatMap(k ->
                        productRepositories.findByName(k.getKeywordProduct())
                                .switchIfEmpty(
                                        Mono.error(
                                                new ResponseStatusException(
                                                        HttpStatus.BAD_REQUEST,
                                                        "El producto no existe: " + k.getKeywordProduct()
                                                )
                                        )
                                )
                                .flatMap(p -> {
                                    if(p.getStock() < k.getKeywordLot()){
                                        return Mono.error(
                                                new ResponseStatusException(
                                                        HttpStatus.BAD_REQUEST,
                                                        "Stock insuficiente"
                                                )
                                        );
                                    }

                                    p.setStock(p.getStock() - k.getKeywordLot());

                                    return productRepositories.save(p)
                                            .retryWhen(
                                                    Retry.max(3)
                                                            .filter(ex -> ex instanceof OptimisticLockingFailureException)
                                                            .doBeforeRetry(rs ->
                                                                    log.warn(
                                                                            "Conflicto de concurrencia en producto {}, reintentando... intento {}",
                                                                            k.getKeywordProduct(),
                                                                            rs.totalRetries() + 1
                                                                    )
                                                            )
                                            )
                                            .flatMap(s -> Mono.just(ProductDto.builder()
                                                    .name(k.getKeywordProduct())
                                                    .lot(k.getKeywordLot().longValue())
                                                    .amount(s.getPrice() * k.getKeywordLot())
                                                    .build()));
                                })
                )
                .map(ProductDto::getAmount)
                .reduce(0.0, Double::sum)
                .flatMap(total -> orderRepositories
                        .save(orderBuilder
                                .buildOfOrderRequestSuccess(orderRequest, total)))
                .map(orderBuilder::buildOfOrder)
                .doOnSuccess(r -> log.info("Orden registrada: {}", r))
                .doOnError(e -> log.error("Error registrando orden", e));

    }

    @Override
    public Mono<Flux<OrderResponse>> listOrder() {
        return Mono.just(orderRepositories.findAll().map(orderBuilder::buildOfOrder));
    }

}
