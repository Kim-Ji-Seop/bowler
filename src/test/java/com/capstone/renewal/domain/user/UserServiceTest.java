package com.capstone.renewal.domain.user;

import com.capstone.renewal.domain.user.dto.request.DuplicationUidRequest;
import com.capstone.renewal.domain.user.dto.request.SignUpRequest;
import com.capstone.renewal.domain.user.dto.response.SignUpResponse;
import com.capstone.renewal.domain.user.repository.UserRepository;
import com.capstone.renewal.domain.user.service.UserService;
import com.capstone.renewal.global.error.BaseException;
import com.capstone.renewal.global.error.ErrorCode;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserServiceTest {
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserService userService;

    @Test
    void uidDuplicationCheck_WhenUidIsEmpty_ShouldThrowException() {
        // given
        DuplicationUidRequest request = new DuplicationUidRequest("");

        // when & then
        BaseException exception = assertThrows(BaseException.class, () -> {
            userService.uidDuplicationCheck(request);
        });

        assertEquals(ErrorCode.INVALID_UID_IS_EMPTY, exception.getErrorCode());
    }


    @Test
    void strIsEmpty_WhenStringIsEmpty_ShouldReturnTrue() {
        // given
        String str = "";

        // when
        boolean result = userService.strIsEmpty(str);

        // then
        assertTrue(result);
    }

    @Test
    void strIsEmpty_WhenStringIsNotEmpty_ShouldReturnFalse() {
        // given
        String str = "not empty";

        // when
        boolean result = userService.strIsEmpty(str);

        // then
        assertFalse(result);
    }
    @Test
    void sign_up_success(){
        //given
        String uid = "test1";
        String password = "12345";
        String name = "test2";
        String nickname = "test3";
        //when
        SignUpRequest req = new SignUpRequest(uid,password,name,nickname);

        SignUpResponse res = userService.insertUserAndReturn(req);
        //then
        assertEquals(1L,res.id());
    }
}