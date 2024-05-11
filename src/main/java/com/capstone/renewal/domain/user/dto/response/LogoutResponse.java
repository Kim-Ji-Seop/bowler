package com.capstone.renewal.domain.user.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
public record LogoutResponse(
    String uid
) {}
