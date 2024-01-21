package com.example.szssonjunyoung.api.szs.controller;

import com.example.szssonjunyoung.api.szs.dto.request.LoginReq;
import com.example.szssonjunyoung.api.szs.dto.request.SignReq;
import com.example.szssonjunyoung.api.szs.repository.SignRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@DisplayName("회원 가입 및 로그인")
@SpringBootTest
@AutoConfigureMockMvc
class SignControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SignRepository signRepository;

    // 1년짜리 테스트토큰
    private String jwtToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwibmFtZSI6Iu2Zjeq4uOuPmSIsInVzZXJJZCI6ImhvbmcxMjMiLCJyb2xlcyI6IlJPTEVfVVNFUiIsImlzcyI6InN6cyIsImlhdCI6MTcwNTg1MjkzMiwiZXhwIjoxNzM3Mzg4OTMyfQ.AiuC74XXVb1aY-BhInSpa7uSIEGhjLXfelGNJnNMrWw";

    @DisplayName("1.회원 가입")
    @Order(1)
    @Test
    public void userSign() throws Exception {
        // 테스트에 사용할 회원가입 정보
        SignReq signReq = new SignReq();
        signReq.setName("홍길동");
        signReq.setRegNo("860824-1655068");
        signReq.setUserId("hong123");
        signReq.setPassword("password123");

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/szs/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dataObj").value(true))
                .andReturn();

        assertTrue(signRepository.existsByUserId("hong123"));
    }



    @DisplayName("2.회원 가입시 중복아이디")
    @Order(2)
    @Test
    public void userSign_DuplicateUserId() throws Exception {
        // 이미 존재하는 회원 아이디로 회원가입 요청을 보내는 경우의 테스트
        SignReq signReq = new SignReq();
        signReq.setName("김둘리");
        signReq.setRegNo("921108-1582816");
        signReq.setUserId("hong123");  // 이미 존재하는 아이디
        signReq.setPassword("password456");

        mockMvc.perform(MockMvcRequestBuilders.post("/szs/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signReq)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("SZSE1000"))
        ;

        // 해당 계정이 이미 존재하는지 확인
        assertTrue(signRepository.existsByUserId("hong123"));
    }



    @DisplayName("3.가입한 이력이 있는 회원")
    @Order(3)
    @Test
    public void duplicateUserId() throws Exception {
        // UserId만 다를때
        SignReq signReq = new SignReq();
        signReq.setName("홍길동");
        signReq.setRegNo("860824-1655068");
        signReq.setUserId("test123");
        signReq.setPassword("password123");

        mockMvc.perform(MockMvcRequestBuilders.post("/szs/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signReq)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("SZSE1001"))
        ;

    }



    @DisplayName("4.로그인 성공")
    @Order(4)
    @Test
    void userLogin() throws Exception {
        LoginReq loginReq = new LoginReq();
        loginReq.setUserId("hong123");
        loginReq.setPassword("password123");

        mockMvc.perform(MockMvcRequestBuilders.post("/szs/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dataObj").exists())
                .andExpect(jsonPath("$.dataObj.jwtToken").isNotEmpty())
                .andExpect(jsonPath("$.dataObj.grantType").value("Bearer"))
                ;
    }


    @DisplayName("5.틀린 비밀번호 로그인")
    @Order(5)
    @Test
    void userFailLogin() throws Exception {
        // 잘못된 아이디 또는 비밀번호로 로그인 요청 객체 생성
        LoginReq loginReq = new LoginReq();
        loginReq.setUserId("hong123");
        loginReq.setPassword("failpw");

        // 로그인 실패 시의 응답 확인
        mockMvc.perform(MockMvcRequestBuilders.post("/szs/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("SZSE1003"))
        ;
    }

}