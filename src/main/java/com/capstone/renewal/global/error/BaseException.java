package com.capstone.renewal.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class BaseException extends RuntimeException{
    ErrorCode errorCode;
}
