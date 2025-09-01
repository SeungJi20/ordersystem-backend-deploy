package com.example.order_system.ordering.controller;

import com.example.order_system.common.dto.CommonDto;
import com.example.order_system.member.dto.MemberResDto;
import com.example.order_system.ordering.domain.Ordering;
import com.example.order_system.ordering.dto.OrderCreateDto;
import com.example.order_system.ordering.dto.OrderListResDto;
import com.example.order_system.ordering.service.OrderingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ordering")
public class OrderingController {
    private final OrderingService orderingService;
    @PostMapping("/create")
    public ResponseEntity create(@RequestBody List<OrderCreateDto> orderCreateDtos){
        Long id = orderingService.create(orderCreateDtos);

        System.out.println(orderCreateDtos);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                CommonDto.builder()
                        .result(id)
                        .status_code(201)
                        .status_message("주문 생성 완료")
                        .build()
        );
    }


    // 설계 : 토글처럼 눌렀을 때 상세내역이 같이 보이게
    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> findAll(){
        List<OrderListResDto> orderListResDtoList = orderingService.findAll();
        return new ResponseEntity<>(
                CommonDto.builder()
                        .result(orderListResDtoList)
                        .status_code(HttpStatus.OK.value())
                        .status_message("주문목록조회완료")
                        .build() ,HttpStatus.OK);


    }


    // 내 주문목록 조회
    @GetMapping("/myorders")
    public ResponseEntity<?> myOrders(){
        List<OrderListResDto> orderListResDtoList = orderingService.myorders();
        return new ResponseEntity<>(
                CommonDto.builder()
                        .result(orderListResDtoList)
                        .status_code(HttpStatus.OK.value())
                        .status_message("내 주문목록조회 성공")
                        .build() ,HttpStatus.OK);
    }


    @DeleteMapping("/cancel/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> orderCancel(@PathVariable Long id){
        Ordering ordering = orderingService.cancel(id);
        return new ResponseEntity<>(
                CommonDto.builder()
                        .result(ordering.getId())
                        .status_code(HttpStatus.OK.value())
                        .status_message("주문 취소 성공")
                        .build() ,HttpStatus.OK);

    }
}
