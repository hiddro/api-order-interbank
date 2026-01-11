package com.reto.interbank.repository;

import com.reto.interbank.models.entities.Product;
import reactor.core.publisher.Mono;

public interface ProductRepositories extends GenericRepo<Product, Long> {

    Mono<Product> findByName(String name);
}
