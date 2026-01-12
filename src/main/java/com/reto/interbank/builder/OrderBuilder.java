package com.reto.interbank.builder;

import com.reto.interbank.models.entities.Order;
import com.reto.interbank.utils.Validator;
import com.reto.reto.interbank.dto.OrderRequest;
import com.reto.reto.interbank.dto.OrderResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class OrderBuilder {

    public OrderResponse buildOfOrder(Order order){
        return OrderResponse.builder()
                .id(order.getId())
                .date(order.getFecha())
                .total(order.getTotal())
                .state(OrderResponse.StateEnum.fromValue(order.getEstado()))
                .build();
    }

    public Order buildOfOrderRequestSuccess(OrderRequest orderRequest, Double total){
        return Order.builder()
                .fecha(orderRequest.getDate())
                .total(Validator.validatePrice(orderRequest, total))
                .estado(OrderResponse.StateEnum.CONFIRMED.getValue())
                .build();
    }
}
