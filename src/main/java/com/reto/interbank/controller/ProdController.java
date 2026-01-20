package com.reto.interbank.controller;

import com.reto.interbank.service.ProductService;
import com.reto.reto.interbank.dto.ProductRequest;
import com.reto.reto.interbank.dto.ProductResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/producto")
public class ProdController {

    @Autowired
    ProductService productService;

    @PostMapping("/{id}")
    public Mono<ResponseEntity<ProductResponse>> addedProduct (@PathVariable Long id,
                                                               @RequestBody Mono<ProductRequest> productRequest ) {
        return productRequest.flatMap(p -> productService.addedPro(id, p))
                .map(e -> ResponseEntity.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(e)
                );
    }
}
