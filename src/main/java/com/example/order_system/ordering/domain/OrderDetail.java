package com.example.order_system.ordering.domain;

import com.example.order_system.common.domain.BaseTimeEntity;
import com.example.order_system.member.domain.Member;
import com.example.order_system.product.domain.Product;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@Getter
@AllArgsConstructor
@ToString
@Entity
@Builder
public class OrderDetail extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ordering_id")
    private Ordering ordering; // 어떤 주문인지를 알 수 있음

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id") // 디비에서의 이름
    private Product product;

}
