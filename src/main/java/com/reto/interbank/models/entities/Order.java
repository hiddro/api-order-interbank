package com.reto.interbank.models.entities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table("orders")
public class Order {
    @Id
    private Long id;
    private String fecha;
    private Double total;
    private String estado;
    @Version
    private Long version;
}
