package com.capstone.renewal.domain.user.dto.response;

import com.capstone.renewal.global.jwt.TokenDto;
import lombok.Builder;

@Builder
public record LoginResponse(
        String name,
        String nickname,
        int scoreAvg,
        TokenDto token
) {
}
