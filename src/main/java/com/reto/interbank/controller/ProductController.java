package com.reto.interbank.controller;

import com.reto.interbank.service.ProductService;
import com.reto.reto.interbank.api.v1.ProductApi;
import com.reto.reto.interbank.dto.ProductRequest;
import com.reto.reto.interbank.dto.ProductResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class ProductController implements ProductApi {

    @Autowired
    ProductService productService;

    @Override
    public Mono<ResponseEntity<Flux<ProductResponse>>> getProducts(ServerWebExchange exchange) {
        return productService.listProduct()
                .map(e -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(e)
                );
    }

    @Override
    public Mono<ResponseEntity<ProductResponse>> getProductByName(String name, ServerWebExchange exchange) {
        return productService.getByName(name)
                .map(e -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(e)
                );
    }

    @Override
    public Mono<ResponseEntity<ProductResponse>> registerProduct(Mono<ProductRequest> productRequest, ServerWebExchange exchange) {
        return productRequest.flatMap(p -> productService.registerProduct(p))
                .map(e -> ResponseEntity.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(e)
                );
    }

    @Override
    public Mono<ResponseEntity<ProductResponse>> updateProduct(String name, Mono<ProductRequest> productRequest, ServerWebExchange exchange) {
        return productRequest.flatMap(p -> productService.updateProduct(name, p))
                .map(e -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(e)
                );
    }

    @Override
    public Mono<ResponseEntity<Void>> deleteProduct(String name, ServerWebExchange exchange) {
        return productService.deleteProduct(name)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}
