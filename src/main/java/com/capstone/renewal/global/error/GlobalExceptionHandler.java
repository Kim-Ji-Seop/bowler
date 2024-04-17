package com.capstone.renewal.global.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(BaseException.class)
    protected ResponseEntity<ErrorResponseEntity> handleBaseException(BaseException e) {
        return ErrorResponseEntity.toResponseEntity(e.getErrorCode());
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    protected ResponseEntity<ErrorResponseEntity> handleUsernameNotFoundException(UsernameNotFoundException e){
        log.error("User not found: " + e.getMessage());
        return ErrorResponseEntity.toResponseEntity(ErrorCode.USER_NOT_FOUND);
    }


}