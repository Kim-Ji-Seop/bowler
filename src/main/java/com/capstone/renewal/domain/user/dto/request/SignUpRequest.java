package com.capstone.renewal.domain.user.dto.request;

public record SignUpRequest(
        String uid,
        String password,
        String name,
        String nickname

) {
}
