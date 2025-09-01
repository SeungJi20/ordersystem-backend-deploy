package com.example.order_system.ordering.service;

import com.example.order_system.common.service.SseAlarmService;
import com.example.order_system.member.domain.Member;
import com.example.order_system.member.repository.MemberRepository;
import com.example.order_system.ordering.domain.OrderDetail;
import com.example.order_system.ordering.domain.Ordering;
import com.example.order_system.ordering.dto.OrderCreateDto;

import com.example.order_system.ordering.dto.OrderListResDto;
import com.example.order_system.ordering.repository.OrderDetailRepository;
import com.example.order_system.ordering.repository.OrderingRepository;
import com.example.order_system.product.domain.Product;
import com.example.order_system.product.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderingService {
    private final OrderingRepository orderingRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final OrderDetailRepository orderDetailRepository;

    private final SseAlarmService  sseAlarmService;

    public Long create(List<OrderCreateDto> orderCreateDtoList) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName(); // 로그인되면 email은 언제든지 꺼낼 수 있음
        Member member = memberRepository.findByEmail(email).orElseThrow(()->new EntityNotFoundException("없는 id입니다."));

        Ordering ordering = Ordering.builder()
                .member(member)
                .build();
        orderingRepository.save(ordering);


        // 밑에가 redis를 사용함으로써, 필요없음
        for(OrderCreateDto dto : orderCreateDtoList) {
            Product product = productRepository.findById(dto.getProductId()).orElseThrow(()-> new EntityNotFoundException("없는 id 입니다"));
            if(product.getStockQuantity() < dto.getProductCount()){
                // 예외를 강제 발생시킴으로서, 모든 임시저장사항들을 rollback 처리
                throw new IllegalArgumentException("재고가 부족합니다"); // 전체취소 하는게 맞으니까 예외전파
            }

            // 1. 동시에 접근하는 상황에서 update 값의 정합성이 깨지고 갱신이상이 발생
            // 2. spring버전이나 mysql버전에 따라 jpa에서 강제에러(deadlock)를 유발시켜 대부분의 요청실패되는 상황이 발생
            product.updateStockQuantity(dto.getProductCount());
            OrderDetail orderDetail = OrderDetail.builder()
                    .product(product)
                    .quantity(dto.getProductCount())
                    .ordering(ordering)
                    .build();

            // 밑에는 cascading 방식
            ordering.getOrderDetailList().add(orderDetail);

        }

//            Product product = orderingRepository.save(orderCreateDto);
        orderingRepository.save(ordering);
        // 주문 성공시 admin 유저에게 알림메시지 전송
        //                                                나중에 여기는 동적으로
        sseAlarmService.publishMessage("admin@naver.com", email, ordering.getId());
        // 큐에 메시지를 담는다.
            return ordering.getId();
    }




// 아래처럼 완전 줄일 수도 있음
    public List<OrderListResDto> findAll(){
        return orderingRepository.findAll().stream().map(o->OrderListResDto.fromEntity(o)).collect(Collectors.toList());
    }


    public List<OrderListResDto> myorders() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findByEmail(email).orElseThrow(()->new EntityNotFoundException("member is not found"));
        return orderingRepository.findAllByMember(member).stream().map(o->OrderListResDto.fromEntity(o)).collect(Collectors.toList());
    }


    public Ordering cancel(Long id){
        // Ordering DB에 상태값 변경 canceled
        Ordering ordering = orderingRepository.findById(id).orElseThrow(()->new EntityNotFoundException("orderingId is not found"));
        ordering.cancelStatus();
        for(OrderDetail orderDetail : ordering.getOrderDetailList()){
            // rdb 재고 업데이트
           orderDetail.getProduct().cancelOrder(orderDetail.getQuantity());

        }
        return ordering;

        }
    }
