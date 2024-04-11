package com.capstone.renewal.global.error;

import com.capstone.renewal.global.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class BaseException extends RuntimeException{
    ErrorCode errorCode;
}
