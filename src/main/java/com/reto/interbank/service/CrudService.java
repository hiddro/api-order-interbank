package com.reto.interbank.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CrudService<T, ID>{
    Mono<T> save(T t);

    Mono<T> update(T t);

    Flux<T> findAll();

    Mono<T> findById(ID id);

    Mono<Void> delete(ID id);
}
