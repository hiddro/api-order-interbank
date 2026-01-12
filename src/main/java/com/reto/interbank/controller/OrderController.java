package com.reto.interbank.controller;

import com.reto.interbank.service.OrderService;
import com.reto.reto.interbank.api.v1.OrderApi;
import com.reto.reto.interbank.dto.OrderRequest;
import com.reto.reto.interbank.dto.OrderResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class OrderController implements OrderApi {

    @Autowired
    OrderService orderService;

    @Override
    public Mono<ResponseEntity<OrderResponse>> registerOrder(Mono<OrderRequest> orderRequest, ServerWebExchange exchange) {
        return orderRequest.flatMap(o -> orderService.registerOrder(o))
                .map(e -> ResponseEntity.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(e)
                );
    }

    @Override
    public Mono<ResponseEntity<Flux<OrderResponse>>> getOrder(ServerWebExchange exchange) {
        return orderService.listOrder()
                .map(e -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(e)
                );
    }

    @Override
    public Mono<ResponseEntity<OrderResponse>> getOrderById(String orderId, ServerWebExchange exchange) {
        return orderService.getById(orderId)
                .map(e -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(e)
                );
    }

    @Override
    public Mono<ResponseEntity<OrderResponse>> updateOrder(String orderId, Mono<OrderRequest> orderRequest, ServerWebExchange exchange) {
        return orderRequest.flatMap(o -> orderService.updateOrder(orderId, o))
                .map(e -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(e)
                );
    }

    @Override
    public Mono<ResponseEntity<Void>> deleteOrder(String orderId, ServerWebExchange exchange) {
        return orderService.deleteOrder(orderId)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}
