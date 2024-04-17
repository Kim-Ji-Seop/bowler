package com.capstone.renewal.domain.user;

import com.capstone.renewal.domain.user.dto.request.DuplicationUidRequest;
import com.capstone.renewal.domain.user.dto.request.LoginRequest;
import com.capstone.renewal.domain.user.dto.request.SignUpRequest;
import com.capstone.renewal.domain.user.dto.response.LoginResponse;
import com.capstone.renewal.domain.user.dto.response.SignUpResponse;
import com.capstone.renewal.global.CustomUserDetailService;
import com.capstone.renewal.global.Role;
import com.capstone.renewal.global.error.BaseException;
import com.capstone.renewal.global.error.ErrorCode;
import com.capstone.renewal.global.jwt.JwtTokenProvider;
import com.capstone.renewal.global.jwt.TokenDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final CustomUserDetailService customUserDetailService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    // 아이디가 존재하면 -> 중복 예외
    // 존재하지 않으면 -> 정상처리 -> Return to UserController
    public boolean uidDuplicationCheck(DuplicationUidRequest request){   // 아이디 빈 값
        if(strIsEmpty(request.uid())) throw new BaseException(ErrorCode.INVALID_UID_IS_EMPTY);
        boolean isDuplicated = userRepository.existsUserEntityByUid(request.uid());
        if(isDuplicated) throw new BaseException(ErrorCode.INVALID_UID_DUPLICATE);
        else return false; // false > 중복된게 없을 때. 성공사례임.
    }
    // 회원가입
    // return : UserIdx, Uid, Name, Nickname
    public SignUpResponse insertUserAndReturn(SignUpRequest request) {
        if(request.password().isEmpty()
                || request.name().isEmpty()
                || request.nickname().isEmpty()){
            throw new BaseException(ErrorCode.INVALID_SOMETHING_IS_EMPTY); // 빈 값
        }

        // 0. 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.password());
        log.info("SignUp Method => before pw : "+request.password() +" | " + "after store pw :" + encodedPassword);
        // 1. 데이터 삽입
        UserEntity newUser = UserEntity.builder()
                .uid(request.uid())
                .password(encodedPassword)
                .name(request.name())
                .nickname(request.nickname())
                .role(Role.USER)
                .build();
        userRepository.save(newUser);

        // 3. Response
        return new SignUpResponse(
                newUser.getUserIdx(),
                newUser.getUid(),
                newUser.getName(),
                newUser.getNickname());
    }

    // 문자열 빈 값 확인
    public boolean strIsEmpty(String str){
        return str.isEmpty();
    }

    public LoginResponse loginUser(LoginRequest request) {
        // 1. 빈 값 확인
        if(request.uid().isEmpty() || request.password().isEmpty()){
            throw new BaseException(ErrorCode.INVALID_SOMETHING_IS_EMPTY); // 빈 값
        }
        // 2. UserDetail 뽑아오기.
        UserDetails userDetails = customUserDetailService.loadUserByUsername(request.uid());

        // 2-1. Password 일치 비교 -> 요청 패스워드를 인코딩한 값 == DB에 저장된 패스워드
        if(!checkPassword(request.password(), userDetails.getPassword())){
            throw new BaseException(ErrorCode.SIGN_IN_NOT_INVALID_PASSWORD);
        }

        // 3. JWT Token 발급(Access, Refresh)
        UserEntity user = (UserEntity) userDetails;
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(user.getRole().toString()));
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
        TokenDto token = jwtTokenProvider.generateToken(authentication, user.getUserIdx());

        // 4. LoginResponse
        return LoginResponse.builder()
                .name(user.getName())
                .nickname(user.getNickname())
                .scoreAvg(user.getScoreAvg())
                .token(token)
                .build();
    }

    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
