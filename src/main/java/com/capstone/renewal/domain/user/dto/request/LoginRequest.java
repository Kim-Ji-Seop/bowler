package com.capstone.renewal.domain.user.dto.request;

public record LoginRequest(
        String uid,
        String password
) {
}
