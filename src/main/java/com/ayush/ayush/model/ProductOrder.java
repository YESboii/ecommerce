package com.ayush.ayush.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class ProductOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer quantity;

    private Double pricePerQuantity;
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @PrePersist
    public void setPricePerQuantity() {
        if (product != null) {
            this.pricePerQuantity = product.getAmount();
        }
    }
}
