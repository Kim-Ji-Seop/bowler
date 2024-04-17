package com.capstone.renewal.global.jwt;

import lombok.Builder;

@Builder
public record TokenDto(
        String grantType,
        String accessToken,
        String refreshToken
) {
}
