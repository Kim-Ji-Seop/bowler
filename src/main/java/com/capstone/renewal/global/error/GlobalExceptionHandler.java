package com.capstone.renewal.global.error;

import com.capstone.renewal.global.error.BaseException;
import com.capstone.renewal.global.error.ErrorResponseEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BaseException.class)
    protected ResponseEntity<ErrorResponseEntity> handleBaseException(BaseException e) {
        return ErrorResponseEntity.toResponseEntity(e.getErrorCode());
    }
}
