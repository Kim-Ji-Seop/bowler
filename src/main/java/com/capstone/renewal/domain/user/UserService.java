package com.capstone.renewal.domain.user;

import com.capstone.renewal.domain.user.dto.request.DuplicationUidRequest;
import com.capstone.renewal.domain.user.dto.request.LoginRequest;
import com.capstone.renewal.domain.user.dto.request.SignUpRequest;
import com.capstone.renewal.domain.user.dto.response.LoginResponse;
import com.capstone.renewal.domain.user.dto.response.LogoutResponse;
import com.capstone.renewal.domain.user.dto.response.SignUpResponse;
import com.capstone.renewal.global.CustomUserDetailService;
import com.capstone.renewal.global.Role;
import com.capstone.renewal.global.error.BaseException;
import com.capstone.renewal.global.error.ErrorCode;
import com.capstone.renewal.global.jwt.JwtTokenProvider;
import com.capstone.renewal.global.jwt.TokenDto;
import com.capstone.renewal.global.redis.RedisDao;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final CustomUserDetailService customUserDetailService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisDao redisDao;
    private final RedisTemplate<String, Object> redisTemplate;
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
        TokenDto token = jwtTokenProvider.generateToken(authentication);

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

    public LogoutResponse logout(String userUid, String jwtToken) {
        // 1. uid로 회원조회
        Optional<UserEntity> user = userRepository.findByUid(userUid);
        // 2. Redis 에서 해당 uid 로 저장된 Refresh Token 이 있는지 여부를 확인
        if (redisDao.getValues(userUid) != null) { // 있을 경우 삭제
            // Refresh Token 삭제
            redisDao.deleteValues(userUid);
        }

        // 3. 해당 Access Token 유효시간 가지고 와서 BlackList 로 저장하기
        Long expiration = jwtTokenProvider.getExpiration(jwtToken);

        redisTemplate.opsForValue()
                .set(jwtToken, "logout", expiration, TimeUnit.MILLISECONDS);

        return LogoutResponse.builder()
                .uid(user.orElseThrow(()->new BaseException(ErrorCode.USER_NOT_EXIST)).getUid())
                .build();
    }

    public LoginResponse reissue(String uid) throws JsonProcessingException {
        // 1. 레디스에 저장된 리프레쉬 토큰을 가져온다.
        String rtkInRedis =redisDao.getValues(uid);
        // 2. 리프레쉬 토큰도 존재하지 않는다면? -> 예외 -> 클라이언트에서는 이걸 받고 로그인 화면으로 넘겨줘야함.
        if (Objects.isNull(rtkInRedis)) {
            throw new BaseException(ErrorCode.EXPIRED_AUTHENTICATION);
        }
        // 3. 리프레쉬 토큰이 존재하다면? -> 기존 리프레쉬 토큰 삭제
        else redisDao.deleteValues(uid);

        String uidFromRtk=jwtTokenProvider.getUserUidFromJWT(rtkInRedis);
        TokenDto tokenDto=jwtTokenProvider.reissueAtk(uid,uidFromRtk);
        Optional<UserEntity> user=userRepository.findByUid(uid);
        return LoginResponse.builder()
                .name(user.get().getName())
                .nickname(user.get().getNickname())
                .scoreAvg(user.get().getScoreAvg())
                .token(tokenDto)
                .build();
    }
}
