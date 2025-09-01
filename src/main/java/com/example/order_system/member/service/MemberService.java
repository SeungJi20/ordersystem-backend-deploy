package com.example.order_system.member.service;

import com.example.order_system.member.domain.Member;
import com.example.order_system.member.dto.LoginReqDto;
import com.example.order_system.member.dto.MemberCreateDto;
import com.example.order_system.member.dto.MemberResDto;
import com.example.order_system.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional // 얘가 있어서 update 자동으로 됨
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder; // config에 만든 password인코더

    public Long save(MemberCreateDto memberCreateDto) {

        if (memberRepository.findByEmail(memberCreateDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        // 저장한 그 객체를 받음(여기 id가 담겨있음)
        String encodedPassword = passwordEncoder.encode(memberCreateDto.getPassword());
        Member member = memberRepository.save(memberCreateDto.toEntity(encodedPassword));
        return member.getId();
    }

    public Member login(LoginReqDto loginReqDto) {

        Optional<Member> optionalMember = memberRepository.findByEmail(loginReqDto.getEmail());
        boolean check = true;
        if (!optionalMember.isPresent()) {
            check = false;
        } else {
            if (!passwordEncoder.matches(loginReqDto.getPassword(), optionalMember.get().getPassword())) { // get으로 꺼냄
                check = false;
            }
        }
        if (!check) {
            System.out.println("로그인 실패 했습니다.");
            throw new IllegalArgumentException("email 또는 비밀번호가 일치하지 않습니다.");
        }
        return optionalMember.get();
    }


    @Transactional(readOnly = true) // readOnly를 붙이는 이유 : 성능을 좋게하기 위해
    public List<MemberResDto> findAll() {

        return memberRepository.findAll().stream()
                .map(m->MemberResDto.fromEntity(m)).collect(Collectors.toList());
//                .map(MemberResDto::fromEntity).collect(Collectors.toList());
    }
// 밑에 주석은 틀린 코드
//    @Transactional(readOnly = true)
//    public MemberResDto findById(Long id) {
//        return memberRepository.findById(id).stream().map(m->MemberResDto.fromEntity(m)).collect(Collectors.toList()).get(0);
//
//    }
    public MemberResDto findById(Long id){
        Member member = memberRepository.findById(id).orElseThrow(()->new EntityNotFoundException("member is not found"));
        return MemberResDto.fromEntity(member);
    }

    @Transactional(readOnly = true)
    public MemberResDto myinfo() throws NoSuchElementException {
        String email = SecurityContextHolder.getContext().getAuthentication().getName(); // 토큰이 있다는 가정하에 진행
        Member member = memberRepository.findByEmail(email).orElseThrow(()-> new EntityNotFoundException("없는 ID 입니다"));
        MemberResDto dto = MemberResDto.fromEntity(member);
        return dto;
    }

    public void delete() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findByEmail(email).orElseThrow(()-> new EntityNotFoundException("member is not found"));
        member.deleteMember("Y");
    }
//        memberRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(()->new EntityNotFoundException("not found")).setDelYn("Y");

}