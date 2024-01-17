package com.example.szssonjunyoung.api.szs.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

@Slf4j
@RestController
public class LoginController {

    @DeleteMapping("/api/v1/user/logout4")
//    @ApiOperation(value = "회원 로그아웃 | (access_token필수)", notes="" +
//            "* 로그아웃을 요청하는 해당 기기의 토큰은 사용불가 처리\n" +
//            "* 그 외 다른 기기는 그대로 유지. \n"
//    )
    public ResponseEntity userLogout22(Locale locale ) {
//        logoutService.logout(request.getHeader("access_token"));
        return ResponseEntity.ok().build();
    }
}
