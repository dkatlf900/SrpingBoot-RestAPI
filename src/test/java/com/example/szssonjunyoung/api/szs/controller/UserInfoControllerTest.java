package com.example.szssonjunyoung.api.szs.controller;

import com.example.szssonjunyoung.api.szs.dto.request.SignReq;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserInfoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // 1년짜리 테스트토큰
    private String jwtToken;


    @BeforeEach
    void setUp() throws Exception {
        jwtToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwibmFtZSI6IuuniOynleqwgCIsInVzZXJJZCI6InRlc3QiLCJyb2xlcyI6IlJPTEVfVVNFUiIsImlzcyI6InN6cyIsImlhdCI6MTcwNTg2MTA4NiwiZXhwIjoxNzM3Mzk3MDg2fQ.NhqdE2tAZCjooArcCSevnSvYqSGwy8L90o_SAa4x_eI";
    }

    /*@DisplayName("1.My 회원정보 조회")
    @Test
    void userInfo() throws Exception {
        SignReq signReq = new SignReq();
        signReq.setName("마징가");
        signReq.setRegNo("880601-2455116");
        signReq.setUserId("test");
        signReq.setPassword("test");

        mockMvc.perform(MockMvcRequestBuilders.post("/szs/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dataObj").value(true))
                .andReturn();

        // 토큰 기반의 인증을 사용하기 때문에 토큰을 헤더에 추가
        mockMvc.perform(MockMvcRequestBuilders.get("/szs/me")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.dataObj.id").value(1))
                .andExpect(jsonPath("$.dataObj.userId").value("test"))
                .andExpect(jsonPath("$.dataObj.name").value("마징가"))
                .andExpect(jsonPath("$.dataObj.regNo").value("880601-2455116"));
    }*/


}