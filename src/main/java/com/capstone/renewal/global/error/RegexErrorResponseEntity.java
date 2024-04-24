package com.capstone.renewal.global.error;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RegexErrorResponseEntity {
    private List<ErrorResponseEntity> regexErrors;
}
