package com.reto.interbank.service;

import com.reto.interbank.models.entities.Order;
import com.reto.reto.interbank.dto.OrderRequest;
import com.reto.reto.interbank.dto.OrderResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OrderService extends CrudService<Order, Long> {

    Mono<OrderResponse> getById(String id);

    Mono<OrderResponse> registerOrder(OrderRequest orderRequest);

    Mono<Flux<OrderResponse>> listOrder();
}
