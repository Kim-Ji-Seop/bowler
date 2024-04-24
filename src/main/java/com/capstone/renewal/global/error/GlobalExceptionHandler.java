package com.capstone.renewal.global.error;

import io.swagger.v3.oas.models.links.Link;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.*;

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
    // 정규식 에러 핸들링 -> @Valid 예외
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<RegexErrorResponseEntity> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        List<ErrorResponseEntity> errorResponseEntities = new LinkedList<>();

        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            String fieldName = error.getField();
            String errorMessage = error.getDefaultMessage();
            ErrorCode errorCode = mapFieldErrorToErrorCode(fieldName);

            ErrorResponseEntity errorResponse = ErrorResponseEntity.builder()
                    .errorTitle(errorCode.name())
                    .code(errorCode.getCode())
                    .errorMessage(errorMessage)
                    .build();
            errorResponseEntities.add(errorResponse);
        }
        return ResponseEntity
                .status(ErrorCode.BAD_REQUEST.getHttpStatus())
                .body(new RegexErrorResponseEntity(errorResponseEntities));
    }

    private ErrorCode mapFieldErrorToErrorCode(String fieldName) {
        return switch (fieldName) {
            case "uid" -> ErrorCode.INVALID_UID_FORMAT;
            case "password" -> ErrorCode.INVALID_PASSWORD_FORMAT;
            case "name", "nickname" -> ErrorCode.INVALID_NAME_FORMAT; // 이름과 별명에 대한 구분 필요 시 다른 ErrorCode 지정 가능
            default -> ErrorCode.BAD_REQUEST;
        };
    }

}