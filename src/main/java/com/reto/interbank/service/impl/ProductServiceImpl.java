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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
    public Mono<ProductResponse> updateProduct(String name, ProductRequest productRequest) {
        return productRepositories.findByName(name)
                .flatMap(p -> productRepositories
                                .save(Product.builder()
                                        .id(p.getId())
                                        .name(productRequest.getName())
                                        .price(productRequest.getPrice())
                                        .stock(productRequest.getStock())
                                        .build()))
                .map(productBuilder::buildOfProduct)
                .switchIfEmpty(
                        Mono.<ProductResponse>error(
                                        new ResponseStatusException(
                                                HttpStatus.BAD_REQUEST,
                                                "El producto no existe"
                                        )
                ))
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
}
