package com.reto.interbank.service;

import com.reto.interbank.models.entities.Product;
import com.reto.reto.interbank.dto.ProductRequest;
import com.reto.reto.interbank.dto.ProductResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductService extends CrudService<Product, Long>{

    Mono<ProductResponse> getByName(String name);

    Mono<ProductResponse> registerProduct(ProductRequest productRequest);

    Mono<Flux<ProductResponse>> listProduct();

    Mono<ProductResponse> updateProduct(String name, ProductRequest productRequest);

    Mono<Void> deleteProduct(String name);
}
