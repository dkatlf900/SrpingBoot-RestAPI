package com.example.szssonjunyoung.api.szs.service;

import com.example.szssonjunyoung.api.szs.dto.request.SignReq;
import com.example.szssonjunyoung.api.szs.entity.UsersEntity;
import com.example.szssonjunyoung.api.szs.repository.SignRepository;
import com.example.szssonjunyoung.base.util.AES256Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Slf4j
@Service("SignService")
public class SignService {

    @Autowired
    private SignRepository signRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 회원가입 허용된 회원정보
     */
    private static final Map<String, String> PERMIT_USERS = new HashMap<>() {{
        put("홍길동", "860824-1655068");
        put("김둘리", "921108-1582816");
        put("마징가", "880601-2455116");
        put("베지터", "910411-1656116");
        put("손오공", "820326-2715702");
    }};


    /**
     * 회원가입
     */
    public boolean signUser(SignReq signReq) {
        if (isPermitUser(signReq.getName(), signReq.getRegNo())) {
            // pw, 주민번호 encode
            signReq.setPassword(passwordEncoder.encode(signReq.getPassword()));
            signReq.setRegNo(AES256Util.encryptAES(signReq.getRegNo()));

            // TODO 가입시 아이디 중복체크 해야한다.
            Optional<UsersEntity> user = signRepository.findByNameAndRegNo(signReq.getName(), signReq.getRegNo());
            if (user.isPresent()) {
                return false;
            }

            signRepository.save(new UsersEntity(signReq));
            return true;
        } else {
            return false;
        }
    }


    /**
     * 가입 허용회원 검증
     */
    private boolean isPermitUser(String name, String regNo) {
        String permitRegNo = PERMIT_USERS.get(name);
        return permitRegNo != null && permitRegNo.equals(regNo);
    }
}
