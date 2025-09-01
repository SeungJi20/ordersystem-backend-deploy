package com.example.order_system.ordering.repository;

import com.example.order_system.member.domain.Member;
import com.example.order_system.ordering.domain.Ordering;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderingRepository extends JpaRepository<Ordering, Long> {
    List<Ordering> findAllByMember(Member member);
}
