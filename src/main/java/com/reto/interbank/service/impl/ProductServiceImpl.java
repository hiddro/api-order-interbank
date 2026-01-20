package com.reto.interbank.service.impl;

import com.reto.interbank.builder.ProductBuilder;
import com.reto.interbank.models.entities.Product;
import com.reto.interbank.repository.GenericRepo;
import com.reto.interbank.repository.ProductRepositories;
import com.reto.interbank.service.ProductService;
import com.reto.reto.interbank.dto.ProductRequest;
import com.reto.reto.interbank.dto.ProductResponse;
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
public class ProductServiceImpl extends CrudServiceImpl<Product, Long> implements ProductService {

    @Autowired
    ProductRepositories productRepositories;

    @Autowired
    ProductBuilder productBuilder;

    @Override
    protected GenericRepo<Product, Long> getRepo() {
        return productRepositories;
    }

    @Override
    public Mono<ProductResponse> getByName(String name) {
        return productRepositories.findByName(name)
                .map(productBuilder::buildOfProduct);
    }

    @Override
    public Mono<ProductResponse> registerProduct(ProductRequest productRequest) {
        return productRepositories.findByName(productRequest.getName())
                .flatMap(p -> Mono.<ProductResponse>error(
                        new ResponseStatusException(
                                HttpStatus.BAD_REQUEST,
                                "El producto ya existe"
                        )
                ))
                .switchIfEmpty(
                    productRepositories
                            .save(productBuilder.buildOfProductRequest(productRequest))
                            .map(productBuilder::buildOfProduct)
                )
                .doOnSuccess(r -> log.info("Producto registrado: {}", r))
                .doOnError(e -> log.error("Error registrando producto", e));
    }

    @Override
    public Mono<Flux<ProductResponse>> listProduct() {
        return Mono.just(productRepositories.findAll().map(productBuilder::buildOfProduct));
    }

    @Override
    public Mono<ProductResponse> updateProduct(String name, String operation, ProductRequest productRequest) {
        return productRepositories.findByName(name)
                .switchIfEmpty(
                        Mono.error(
                                new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "El producto no existe"
                                )
                        ))
                .flatMap(p -> {
                    if(p.getStock() < productRequest.getStock() && operation.equalsIgnoreCase("DECREASE")){
                        return Mono.error(
                                new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "Stock insuficiente"
                                )
                        );
                    }

                    p.setName(productRequest.getName());
                    p.setPrice(productRequest.getPrice());
                    p.setStock(operation.equalsIgnoreCase("DECREASE") ?
                            p.getStock() - productRequest.getStock() :
                            p.getStock() + productRequest.getStock());

                    return productRepositories
                            .save(p);
                })
                .map(productBuilder::buildOfProduct)
                .retryWhen(
                        Retry
                                .max(3) // hasta 3 reintentos
                                .filter(ex -> ex instanceof OptimisticLockingFailureException)
                                .doBeforeRetry(rs ->
                                        log.warn("Conflicto de concurrencia, reintentando... intento {}",
                                                rs.totalRetries() + 1)
                                )
                )
                .onErrorMap(
                        OptimisticLockingFailureException.class,
                        e -> new ResponseStatusException(
                                HttpStatus.CONFLICT,
                                "El producto fue modificado por otro proceso, intente nuevamente"
                        )
                )
                .doOnSuccess(r -> log.info("Producto registrado: {}", r))
                .doOnError(e -> log.error("Error registrando producto", e));
    }

    @Override
    public Mono<Void> deleteProduct(String name) {
        return productRepositories.findByName(name)
                .switchIfEmpty(
                        Mono.error(
                                new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "El producto no existe"
                                ))
                )
                .flatMap(productRepositories::delete)
                .doOnSuccess(v -> log.info("Delete completado"))
                .doOnError(e -> log.error("Error en delete", e));
    }

    @Override
    public Mono<ProductResponse> addedPro(Long id, ProductRequest productRequest) {
        return productRepositories.findById(id)
                .switchIfEmpty(
                        Mono.error(
                                new ResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "El producto no existe"
                                ))
                )
                .flatMap(p -> {
                    p.setStock(p.getStock() + productRequest.getStock());

                    return productRepositories
                            .save(p);
                })
                .map(productBuilder::buildOfProduct)
                .doOnSuccess(v -> log.info("Delete completado"))
                .doOnError(e -> log.error("Error en delete", e));
    }

}
