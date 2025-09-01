package com.example.order_system.common.auth;

import com.example.order_system.common.dto.CommonErrorDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;

// 401 에러인 경우 잡아내겠다!
@Component
@Slf4j
public class JwtAuthenticationHandler implements AuthenticationEntryPoint { // 이 에러는 공식문서 찾아봐야
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.error(authException.getMessage());
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 이게 401임 헤더부분에 상태코드 세팅한 것
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        CommonErrorDto dto = new CommonErrorDto(401, "token 없거나 유효하지 않습니다.");
        ObjectMapper objectMapper = new ObjectMapper();
        String body = objectMapper.writeValueAsString(dto);
        PrintWriter printWriter = response.getWriter();
        printWriter.write(body);
        printWriter.flush();
    }
}
