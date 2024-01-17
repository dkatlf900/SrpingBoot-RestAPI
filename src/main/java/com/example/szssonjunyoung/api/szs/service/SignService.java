package com.example.szssonjunyoung.api.szs.service;

import com.example.szssonjunyoung.api.szs.dto.request.SignReq;
import com.example.szssonjunyoung.api.szs.entity.Users;
import com.example.szssonjunyoung.api.szs.repository.SignRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service("SignService")
public class SignService {

    @Autowired
    private SignRepository signRepository;

    public boolean signUser(SignReq signReq) {
        // TODO 검증로직 들어가야하고 aop로 에러발생시 잡아야 한다.
        // 회원가입 로직을 구현합니다.
        signRepository.save(new Users(signReq));
        return true; // 성공 여부를 반환
    }
}
