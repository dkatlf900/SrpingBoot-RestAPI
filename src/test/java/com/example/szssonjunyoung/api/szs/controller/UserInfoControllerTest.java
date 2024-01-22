package com.example.szssonjunyoung.api.szs.controller;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@DisplayName("토큰검증")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserInfoControllerTest {

    @Autowired
    private MockMvc mockMvc;


    private String jwtToken = "etestetstesstsetstests";




    @DisplayName("jwt token 검증(유효하지 않은 토큰)")
    @Test
    @Rollback(value = false)
    void userInfo() throws Exception {

        // 토큰 기반의 인증을 사용하기 때문에 토큰을 헤더에 추가
        mockMvc.perform(MockMvcRequestBuilders.get("/szs/me")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

    }


}