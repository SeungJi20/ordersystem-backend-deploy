package com.example.order_system.member.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RefreshTokenDto {
    private String refreshToken;
}
// 한개밖에 없으면 파라미터로 하면 되지 않나 -> 보안상 위험