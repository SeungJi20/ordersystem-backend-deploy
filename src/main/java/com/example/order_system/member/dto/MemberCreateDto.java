package com.example.order_system.member.dto;

import com.example.order_system.member.domain.Member;
import com.example.order_system.member.domain.Role;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class MemberCreateDto {
    @NotEmpty(message = "이름은 필수 입력 항목입니다.")
    private String name;
    @NotEmpty(message = "email은 필수 입력 항목입니다.")
    private String email;
    @NotEmpty(message = "password는 필수 입력 항목입니다.")
    @Size(min = 8, message = "password의 길이가 너무 짧습니다")
    private String password;


    // 암호화하면 암호화된 패스워드 받아야 함
    public Member toEntity(String encodedPassword) { // 외부에서 값 받을 필요 없으니 toEntity() 괄호 안에 값은 비어있어야
        return Member.builder()
                .name(this.getName())
                .email(this.getEmail())
                .password(encodedPassword)
                .role(Role.USER)
                .build();
    }
}
