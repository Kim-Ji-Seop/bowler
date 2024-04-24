package com.capstone.renewal.domain.user.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public record DuplicationUidRequest(
        @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9]{0,15}$", message = "아이디는 영문자로 시작하고 영문자 또는 숫자를 포함할 수 있으며 최대 16자입니다.")
        String uid
){}
