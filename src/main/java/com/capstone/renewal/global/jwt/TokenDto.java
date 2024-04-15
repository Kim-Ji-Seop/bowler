package com.capstone.renewal.global.jwt;

public record TokenDto(
        String grantType,
        String accessToken,
        String refreshToken
) {
}
