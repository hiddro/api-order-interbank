package com.reto.interbank.utils;

import com.reto.reto.interbank.dto.OrderRequest;
import com.reto.reto.interbank.dto.RegisterProductKeywordItem;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class Validator {

    public static Double validatePrice (OrderRequest orderRequest, Double total){
        long contador = orderRequest.getProductKeywords().getKeywords().stream()
                .collect(Collectors.groupingBy(
                        RegisterProductKeywordItem::getKeywordProduct,
                        Collectors.summingInt(RegisterProductKeywordItem::getKeywordLot)
                ))
                .size();

        return calculateDiscount(contador, total);
    }

    public static Double calculateDiscount(Long contador, Double total) {
        if(total >= 1000){
           total = total - (total * 0.1);
        }

        if(contador >= 5){
            total = total - (total * 0.05);
        }
        return total;
    }
}
