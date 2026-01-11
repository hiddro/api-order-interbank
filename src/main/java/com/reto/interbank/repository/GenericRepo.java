package com.reto.interbank.repository;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

@NoRepositoryBean
public interface GenericRepo<T, ID> extends ReactiveCrudRepository<T, ID> {
}
