package com.example.order_system.member.dto;

import com.example.order_system.member.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder
public class MemberResDto {
    private Long id;
    private String email;
    private String name;

    public static MemberResDto fromEntity(Member member){
        return MemberResDto.builder()
                .id(member.getId())
                .email(member.getEmail())
                .name(member.getName())
                .build();

    }
}
