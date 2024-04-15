package com.capstone.renewal.domain.user;

import com.capstone.renewal.domain.user.dto.request.DuplicationUidRequest;
import com.capstone.renewal.domain.user.dto.request.SignUpRequest;
import com.capstone.renewal.domain.user.dto.response.SignUpResponse;
import com.capstone.renewal.global.error.BaseException;
import com.capstone.renewal.global.error.ErrorCode;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    // 아이디가 존재하면 -> 중복 예외
    // 존재하지 않으면 -> 정상처리 -> Return to UserController
    public boolean uidDuplicationCheck(DuplicationUidRequest request){   // 아이디 빈 값
        if(strIsEmpty(request.uid())) throw new BaseException(ErrorCode.INVALID_UID_IS_EMPTY);
        boolean isDuplicated = userRepository.existsUserEntityByUid(request.uid());
        if(isDuplicated) throw new BaseException(ErrorCode.INVALID_UID_DUPLICATE);
        else return false; // false > 중복된게 없을 때. 성공사례임.
    }

    public SignUpResponse insertUserAndReturn(SignUpRequest request) {
        if(strIsEmpty(request.password())
                || strIsEmpty(request.name())
                || strIsEmpty(request.nickname()))
            throw new BaseException(ErrorCode.INVALID_SOMETHING_IS_EMPTY); // 빈 값

        // 1. 데이터 삽입
        UserEntity newUser = UserEntity.builder()
                .uid(request.uid())
                .password(request.password())
                .name(request.name())
                .nickname(request.nickname())
                .build();
        userRepository.save(newUser);

        // 3. Response
        return new SignUpResponse(
                newUser.getUserIdx(),
                newUser.getUid(),
                newUser.getName(),
                newUser.getNickname());
    }

    public boolean strIsEmpty(String str){
        return str.isEmpty();
    }
}
