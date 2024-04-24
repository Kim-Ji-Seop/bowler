package com.capstone.renewal.domain.user.dto.request;

import jakarta.validation.constraints.Pattern;

public record SignUpRequest(
        @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9]{0,15}$", message = "아이디는 영문자로 시작하고 영문자 또는 숫자를 포함할 수 있으며 최대 16자입니다.")
        String uid,
        @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*])[A-Za-z0-9!@#$%^&*]{8,16}$", message = "비밀번호는 8~16자리여야 하며, 최소 하나의 문자, 숫자, 특수문자를 포함해야 합니다.")
        String password,
        @Pattern(regexp = "^[가-힣a-zA-Z]{1,10}$", message = "이름은 한글 또는 영문자로 이루어져 있어야 하며, 최대 10자입니다.")
        String name,
        @Pattern(regexp = "^[가-힣a-zA-Z]{1,10}$", message = "별명은 한글 또는 영문자로 이루어져 있어야 하며, 최대 10자입니다.")
        String nickname

) {
}
