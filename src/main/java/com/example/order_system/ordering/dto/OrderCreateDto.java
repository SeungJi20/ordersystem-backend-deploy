package com.example.order_system.ordering.dto;

import com.example.order_system.member.domain.Member;
import com.example.order_system.member.domain.Role;
import com.example.order_system.ordering.domain.Ordering;
import com.example.order_system.product.domain.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/* 데이터 형식
[ {"productId" : 1, "productCount" : 3},  {"productId" : 2, "productCount" : 5}]
=> dto에는 productId, productCount만 정의하고 받아올때 List로 받아오면 됨
여기서 List로 받아서 넘겨도 안되는건 아님~
 */
@AllArgsConstructor
@NoArgsConstructor
@Data

// 리턴은 아니니까 Builder는 필요없음
public class OrderCreateDto { // 배열형식으로 들어오는것
    private Long productId;
    private Integer productCount;

//    private List<Product> productList;
//
//    @AllArgsConstructor
//    @NoArgsConstructor
//    @Data
//    private static class Product{
//        private Long ProductId;
//        private Integer productCount;
//    }

}


/*
// 밑에는 새로운 데이터 형식 만들어본 것!
//package com.example.order_system.ordering.dto;
 */
//
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import java.util.List;
//
//// 만약 형식이 이런 구조라면
///* 데이터 형식
//{"details" : [ {"productId":1, "productCount":3}, {"productId":2, "productCount":4 }],
//"storeId" :1, "payment" : "kakao"}
// */
//
//@AllArgsConstructor
//@NoArgsConstructor
//@Data
//
//public class OrderDetailDto {
//    private Long storeId;
//    private String payment;
//
//    private List<Product> details; // 그 안에 객체 근데 그 객체가 여러군데에서 쓰면 아예 밖으로 빼면 됨
//
//    @AllArgsConstructor
//    @NoArgsConstructor
//    @Data
//    private static class Product{ // 독립적이지 않은거 같다! -> static
//        private Long productId;
//        private Integer productCount;
//
//        // 독립적이면 밖으로 빼기
//
//        // static 변수 vs static 클래스
//    }
//}



