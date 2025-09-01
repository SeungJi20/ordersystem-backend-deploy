package com.example.order_system.member.repository;

import com.example.order_system.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member,Long> { // 이것만 있으면 됨!

    Optional<Member> findByEmail(String email);
}
