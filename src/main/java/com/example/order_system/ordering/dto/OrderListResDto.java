package com.example.order_system.ordering.dto;


import com.example.order_system.ordering.domain.OrderDetail;
import com.example.order_system.ordering.domain.OrderStatus;
import com.example.order_system.ordering.domain.Ordering;
import com.example.order_system.product.domain.Product;
import com.example.order_system.product.dto.ProductResDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OrderListResDto {
    private Long id;
    private String memberEmail;
    private OrderStatus orderStatus;
    private List<OrderDetailResDto> orderDetails;

//    @AllArgsConstructor
//    @NoArgsConstructor
//    @Data
//    private static class OrderDetailResDto{
//        private Long detailId;
//        private String productName;
//        private Integer productCount;
//    }

    public static OrderListResDto fromEntity(Ordering ordering) {
//        List<OrderDetail> orderDetailList = ordering.getOrderDetailList();
        List<OrderDetailResDto> orderDetailResDtoList = new ArrayList<>();
        for (OrderDetail orderDetail : ordering.getOrderDetailList()) {
            orderDetailResDtoList.add(OrderDetailResDto.fromEntity(orderDetail));
        }
        OrderListResDto dto = OrderListResDto.builder()
                .id(ordering.getId())
                .memberEmail(ordering.getMember().getEmail())
                .orderStatus(ordering.getOrderStatus())
//                    .orderDetails(ordering.getOrderDetailList()) // 이거 안됨
                .orderDetails(orderDetailResDtoList)
                .build();
        return dto;

    }
}
