package com.capstone.renewal.global.error;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.ResponseEntity;

@Data
@Builder
public class ErrorResponseEntity {
    private String errorTitle;
    private int code;
    private String errorMessage;

    public static ResponseEntity<ErrorResponseEntity> toResponseEntity(ErrorCode e){
        return ResponseEntity
                .status(e.getHttpStatus())
                .body(ErrorResponseEntity.builder()
                        .errorTitle(e.name())
                        .code(e.getCode())
                        .errorMessage(e.getErrorMessage())
                        .build()
                );
    }
}
