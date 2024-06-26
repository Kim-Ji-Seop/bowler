package com.capstone.renewal.global;

import com.capstone.renewal.global.error.ErrorCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
@Getter
@AllArgsConstructor
@JsonPropertyOrder({"httpStatus", "code", "message", "result"})
public class BaseResponse<T> {
    private final String message;
    private final int code;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T result;

    public BaseResponse(T result) {
        this.message = ErrorCode.SUCCESS.getErrorMessage();
        this.code = ErrorCode.SUCCESS.getCode();
        this.result = result;
    }
    public BaseResponse(ErrorCode errorCode) {
        this.code=errorCode.getCode();
        this.message = errorCode.getErrorMessage();
    }

}
