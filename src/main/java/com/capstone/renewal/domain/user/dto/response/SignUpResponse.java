package com.capstone.renewal.domain.user.dto.response;

public record SignUpResponse(
        long id,
        String uid,
        String name,
        String nickname
) {
}
