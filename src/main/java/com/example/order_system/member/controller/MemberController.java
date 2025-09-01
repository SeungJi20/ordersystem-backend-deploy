package com.example.order_system.member.controller;

import com.example.order_system.common.auth.JwtTokenProvider;
import com.example.order_system.common.dto.CommonDto;
import com.example.order_system.member.domain.Member;
import com.example.order_system.member.dto.*;
import com.example.order_system.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/create")
    public ResponseEntity<?> save(@RequestBody @Valid MemberCreateDto memberCreateDto) {
        Long id = memberService.save(memberCreateDto);
        return new ResponseEntity<>(
                CommonDto.builder()
                        .result(id)
                        .status_code(HttpStatus.CREATED.value())
                        .status_message("회원가입완료")
                        .build()
                , HttpStatus.CREATED); // json이니까 requestBody
        // 그냥 ok말고 id

    }


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/detail/{id}")
    public ResponseEntity<?> memberDetail(@PathVariable Long id){
        return new ResponseEntity<>(
                CommonDto.builder()
                        .result(memberService.findById(id))
                        .status_code(HttpStatus.OK.value())
                        .status_message("회원상세조회완료")
                        .build(),
                HttpStatus.OK);
    }


    @PostMapping("/doLogin")
    public ResponseEntity<?> doLogin(@RequestBody LoginReqDto loginReqDto) {

           Member member = memberService.login(loginReqDto);

            // at 토큰 생성 -> 헤더에 넣었음
            String accessToken = jwtTokenProvider.createAtToken(member);
            // rt 토큰 생성
            String refreshToken  =   jwtTokenProvider.createRtToken(member);

            LoginResDto loginResDto = LoginResDto.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();

            return new ResponseEntity<>(
                    CommonDto.builder()
                            .result(loginResDto)
                            .status_code(HttpStatus.OK.value())
                            .status_message("로그인성공") // 꺼낼 때 .result.accessToken 이런식으로 꺼내야
                            .build()

                    ,HttpStatus.OK);
    }

    // rt를 통한 at 갱신 요청
    @PostMapping("/refresh-at")
    public ResponseEntity<?> generateNewAt(@RequestBody RefreshTokenDto refreshTokenDto) {
        // rt 검증 로직
        Member member = jwtTokenProvider.validateRt(refreshTokenDto.getRefreshToken());
        // at 신규 생성
        String accessToken = jwtTokenProvider.createAtToken(member); // rt를 넘겨오면
        LoginResDto loginResDto = LoginResDto.builder()
                .accessToken(accessToken)
                .build();

        return new ResponseEntity<>(
                CommonDto.builder()
                        .result(loginResDto)
                        .status_code(HttpStatus.OK.value())
                        .status_message("at 재발급 성공") // 꺼낼 때 .result.accessToken 이런식으로 꺼내야
                        .build()

                ,HttpStatus.OK);
    }


    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> findAll(){
        List<MemberResDto> memberResDtoList = memberService.findAll();
        return new ResponseEntity<>(
                CommonDto.builder()
                        .result(memberResDtoList)
                        .status_code(HttpStatus.OK.value())
                        .status_message("회원목록조회완료")
                        .build() ,HttpStatus.OK);


    }


//    public List<MemberResDto> findAll(){
//        return memberService.findAll();
//    }

    @GetMapping("/myinfo")
    public ResponseEntity<?> myinfo(){

        return new ResponseEntity<>(
                CommonDto.builder().result(memberService.myinfo()).status_code(HttpStatus.OK.value()).status_message("회원 정보 조회 완료").build(), HttpStatus.OK);

//        return new ResponseEntity<>(memberService.myinfo(), HttpStatus.OK);
//    }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(){
        memberService.delete();

        return new ResponseEntity<>(
//                CommonDto.builder().result(memberService.delete()).status_code(HttpStatus.OK.value()).status_message("회원 삭제 완료").build(), HttpStatus.OK);
        CommonDto.builder().result("ok").status_code(HttpStatus.OK.value()).status_message("회원 삭제 완료").build(), HttpStatus.OK);
    }



}
