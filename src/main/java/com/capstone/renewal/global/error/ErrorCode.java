package com.capstone.renewal.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
@AllArgsConstructor
@Getter
public enum ErrorCode {
    // 고정 코드
    SUCCESS(HttpStatus.OK,200,  "요청에 성공하였습니다."),
    BAD_REQUEST( HttpStatus.BAD_REQUEST,400,  "입력값을 확인해주세요."),
    FORBIDDEN(HttpStatus.FORBIDDEN,  403,"권한이 없습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, 404,"대상을 찾을 수 없습니다."),

    // 커스텀 코드
    // 회원가입 - 정규표현식
    INVALID_UID_FORMAT(HttpStatus.BAD_REQUEST,4000,"아이디 정규 표현식 예외입니다."),
    // 회원가입 - 중복확인
    INVALID_UID_DUPLICATE(HttpStatus.BAD_REQUEST,4001,"중복된 아이디 입니다."),
    // 회원가입 - 중복확인 > 아이디 빈값
    INVALID_UID_IS_EMPTY(HttpStatus.BAD_REQUEST,4002,"아이디를 입력해주세요."),
    // 회원가입 > 빈 값 존재
    INVALID_SOMETHING_IS_EMPTY(HttpStatus.BAD_REQUEST,4003,"입력값을 확인해주세요."),
    // 회원가입 > 알수없는 오류 -> 서버잘못
    SIGN_UP_ERROR_SOMETHING_ELSE(HttpStatus.INTERNAL_SERVER_ERROR,5000,"알수없는 오류입니다. 다시 시도해주세요.");

    private final HttpStatus httpStatus;
    private final int code;
    private final String errorMessage;

}
