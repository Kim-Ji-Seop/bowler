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
    // 회원가입 - 중복확인
    INVALID_UID_DUPLICATE(HttpStatus.BAD_REQUEST,4001,"중복된 아이디 입니다."),
    // 회원가입 - 중복확인 > 아이디 빈값
    INVALID_UID_IS_EMPTY(HttpStatus.BAD_REQUEST,4002,"아이디를 입력해주세요."),
    // 회원가입 > 빈 값 존재
    INVALID_SOMETHING_IS_EMPTY(HttpStatus.BAD_REQUEST,4003,"입력값을 확인해주세요."),
    // 로그인 - 비밀번호 틀림
    SIGN_IN_NOT_INVALID_PASSWORD(HttpStatus.BAD_REQUEST, 4004,"비밀번호가 틀립니다."),
    // 로그인 - 일치하는 유저가 없을때
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, 4005, "해당 사용자를 찾을 수 없습니다."),
    // 회원가입 - 정규표현식
    INVALID_UID_FORMAT(HttpStatus.BAD_REQUEST, 4006, "아이디는 영문자로 시작하고 영문자 또는 숫자를 포함할 수 있으며 최대 16자입니다."),
    INVALID_PASSWORD_FORMAT(HttpStatus.BAD_REQUEST, 4007, "비밀번호는 8~16자리여야 하며, 최소 하나의 문자, 숫자, 특수문자를 포함해야 합니다."),
    INVALID_NAME_FORMAT(HttpStatus.BAD_REQUEST, 4008, "이름은 한글 또는 영문자로 이루어져 있어야 하며, 최대 10자입니다."),
    INVALID_NICKNAME_FORMAT(HttpStatus.BAD_REQUEST, 4009, "별명은 한글 또는 영문자로 이루어져 있어야 하며, 최대 10자입니다."),
    // jwt
    TOKEN_NOT_EXIST(HttpStatus.UNAUTHORIZED, 401, "JWT Token이 존재하지 않습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED,  401,"유효하지 않은 JWT Token 입니다."),
    ACCESS_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED,  401,"만료된 Access Token 입니다."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED,  401,"만료된 Refresh Token 입니다."),
    FAIL_AUTHENTICATION(HttpStatus.UNAUTHORIZED,  403,"사용자 인증에 실패하였습니다."),
    EXPIRED_AUTHENTICATION(HttpStatus.UNAUTHORIZED,403,"인증정보가 만료되었습니다."),
    // 로그아웃
    USER_NOT_EXIST(HttpStatus.NOT_FOUND,4011,"존재하지 않는 유저입니다."),

    // 회원가입 > 알수없는 오류 -> 서버잘못
    SIGN_UP_ERROR_SOMETHING_ELSE(HttpStatus.INTERNAL_SERVER_ERROR,5000,"알수없는 오류입니다. 다시 시도해주세요.");

    private final HttpStatus httpStatus;
    private final int code;
    private final String errorMessage;

}
