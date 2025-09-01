package com.example.order_system.product.domain;

import com.example.order_system.common.domain.BaseTimeEntity;
import com.example.order_system.member.domain.Member;
import com.example.order_system.product.dto.ProductUpdateDto;
import jakarta.persistence.*;
import lombok.*;


@NoArgsConstructor
@Getter
@AllArgsConstructor
@ToString
@Entity
@Builder
public class Product extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String category;
    private int price;
    private int stockQuantity;
    private String imagePath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id")
    private Member member;

    public void updateImageUrl(String imgUrl){
        this.imagePath = imgUrl;
    }
    public void updateProduct(ProductUpdateDto productUpdateDto) {
        this.name = productUpdateDto.getName();
        this.category = productUpdateDto.getCategory();
        this.price = productUpdateDto.getPrice();
        this.stockQuantity = productUpdateDto.getStockQuantity();

    }

    public void updateStockQuantity(int orderQuantity) {
        this.stockQuantity = this.stockQuantity - orderQuantity;
    }

    public void cancelOrder(int orderQuantity){ // 취소할 때
        this.stockQuantity = this.stockQuantity + orderQuantity;
    }
}
