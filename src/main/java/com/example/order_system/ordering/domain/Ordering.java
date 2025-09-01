package com.example.order_system.ordering.domain;

import com.example.order_system.common.domain.BaseTimeEntity;
import com.example.order_system.member.domain.Member;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@AllArgsConstructor
@ToString
@Entity
@Builder
public class Ordering extends BaseTimeEntity {
    // Ordering은 1:N에서 1이니까 필요한지 안필요한지 봐야하는데
    // cascading하면 one to many 필요
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING) // 기본적으로 enum이니까
    @Builder.Default // 빌더니까 이거 무조건!
    private OrderStatus orderStatus = OrderStatus.ORDERED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    // OneToMany는 mappedBy 필수임
    @OneToMany(mappedBy = "ordering", cascade = CascadeType.PERSIST, orphanRemoval = true)
    @Builder.Default
    private List<OrderDetail> orderDetailList = new ArrayList<>(); // cascading하려면 초기화 필수!!

    public void cancelStatus(){
        this.orderStatus = OrderStatus.CANCELED;
    }

}
