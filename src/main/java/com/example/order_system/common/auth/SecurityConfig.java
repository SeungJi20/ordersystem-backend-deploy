package com.example.order_system.common.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig { // 이게 필터체인

    private final JwtTokenFilter jwtTokenFilter;

    private final JwtAuthorizationHandler jwtAuthorizationHandler;
    private final JwtAuthenticationHandler jwtAuthenticationHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception { // SecurityFilterChain과 httpSecurity 둘이 상속관계 일 것
        return httpSecurity
                // cors : 특정도메인에 대한 허용정책, postman은 cors정책에 적용X
                // cors : filter레벨에서 걸리는 것임
                .cors(c->c.configurationSource(corsConfiguration()))
                // csrf : 보안공격 중 하나로서, 타 사이트의 쿠키값을 꺼내서 탈취하는 공격
                // 세션 기반 로그인(mvc패턴, ssr)에서는 csrf 별도 설정하는 것이 일반적 : 타임리프, jsp 쓰는 서버
                // 토큰 기반 로그인(rest api서버, csr)에서는 csrf 설정하지 않는 것이 일반적
                .csrf(AbstractHttpConfigurer::disable) // csrf 비활성화. 저 공격에 대비 안하겠다는 뜻
                // http basic은 email/pw를 인코딩하여 인증하는 방식. 원래 비밀번호는 인코딩 하면 안되긴 함
                // 간단한 인증의 경우에만 사용.
                .httpBasic(AbstractHttpConfigurer::disable)
                // 세션 로그인방식 비활성화. stateless는 토큰을 사용하겠다는 뜻
                .sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // token을 검증하고, token 검증을 통해 Authentication 객체 생성
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(e ->
                        e.authenticationEntryPoint(jwtAuthenticationHandler) // 401의 경우(토큰이 잘못됐을 떄)
                                .accessDeniedHandler(jwtAuthorizationHandler) // 403의 경우

                )
                // 예외 api 정책 설정
                // authenticated() : 예외를 제외한 모든 요청에 대해서 Authentication객체가 생성되기를 요구
                .authorizeHttpRequests(a->a.requestMatchers(
                        "/member/create",
                        "/member/doLogin",
                        "/member/refresh-at",
                        "/product/list",
                        "/health"

                ).permitAll().anyRequest().authenticated())
                .build(); // httpSecurity라는 객체 안에
    }

    private CorsConfigurationSource corsConfiguration(){
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "https://www.seung12.shop"));
        configuration.setAllowedMethods(Arrays.asList("*")); // 모든 HTTP(get, post 등) 메서드 허용
        configuration.setAllowedHeaders(Arrays.asList("*")); // 모든 헤더요소(Authorization 등) 허용
        configuration.setAllowCredentials(true); // 자격 증명 허용
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 url패턴에 대해 cors설정 적용
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

}

