package com.example.szssonjunyoung.api.szs.service;

import com.example.szssonjunyoung.api.szs.dto.request.LoginReq;
import com.example.szssonjunyoung.api.szs.entity.UsersEntity;
import com.example.szssonjunyoung.api.szs.repository.LoginRepository;
import com.example.szssonjunyoung.core.aop.exception.ErrorCode;
import com.example.szssonjunyoung.core.aop.exception.custom.SzsException;
import com.example.szssonjunyoung.core.token.JwtTokenProvider;
import com.example.szssonjunyoung.core.token.TokenInfoRes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service("LoginService")
public class LoginService {

    @Autowired
    private LoginRepository loginRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    public TokenInfoRes login(LoginReq loginReq) {
        // 아이디, 비번 체크
        Optional<UsersEntity> user = loginRepository.findByUserId(loginReq.getUserId());
        if(user.isPresent() && passwordEncoder.matches(loginReq.getPassword(), user.get().getPassword())) {
            // 로그인 성공시 토큰 발급
            TokenInfoRes tokenInfoRes = jwtTokenProvider.createLoginToken(user.get());
            return tokenInfoRes;
        } else {
            throw new SzsException(ErrorCode.ERROR_SZSE1003);
        }
    }
}
