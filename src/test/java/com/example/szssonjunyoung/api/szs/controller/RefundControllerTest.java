package com.example.szssonjunyoung.api.szs.controller;

import com.example.szssonjunyoung.api.szs.dto.request.SignReq;
import com.example.szssonjunyoung.api.szs.dto.response.RefundRes;
import com.example.szssonjunyoung.api.szs.dto.response.SzsScrapRes;
import com.example.szssonjunyoung.base.dto.GeneralResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("회원 환급금액")
@SpringBootTest
@AutoConfigureMockMvc
class RefundControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    private String jwtToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwibmFtZSI6Iuq5gOuRmOumrCIsInVzZXJJZCI6InRlc3Q0Iiwicm9sZXMiOiJST0xFX1VTRVIiLCJpc3MiOiJzenMiLCJpYXQiOjE3MDU4NjE0MjUsImV4cCI6MTczNzM5NzQyNX0.rsnsF-XxdwF_Yuul7YRY6GITkmtrFNJNmjNmqJfKzUw";

    /*@BeforeEach
    void setUp() throws Exception {
        jwtToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwibmFtZSI6Iuq5gOuRmOumrCIsInVzZXJJZCI6InRlc3Q0Iiwicm9sZXMiOiJST0xFX1VTRVIiLCJpc3MiOiJzenMiLCJpYXQiOjE3MDU4NjE0MjUsImV4cCI6MTczNzM5NzQyNX0.rsnsF-XxdwF_Yuul7YRY6GITkmtrFNJNmjNmqJfKzUw";
    }*/


    @DisplayName("My 환급금액")
    @Test
    void userRefund() throws Exception {
        // 테스트에 사용할 회원가입 정보
        SignReq signReq = new SignReq();
        signReq.setName("김둘리");
        signReq.setRegNo("921108-1582816");
        signReq.setUserId("test4");
        signReq.setPassword("test");

        mockMvc.perform(MockMvcRequestBuilders.post("/szs/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dataObj").value(true))
                .andReturn();

        mockMvc.perform(MockMvcRequestBuilders.post("/szs/scrap")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andReturn();


        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/szs/refund")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dataObj.이름").value("김둘리"))
                .andReturn();

        // Assertions
       /* String responseContent = mvcResult.getResponse().getContentAsString();
        GeneralResponse<RefundRes> response = objectMapper.readValue(responseContent, new TypeReference<GeneralResponse<RefundRes>>() {});

        assertNotNull(response);
        assertNotNull(response.getDataObj());
        assertNotNull(response.getDataObj().getName());*/
    }

}