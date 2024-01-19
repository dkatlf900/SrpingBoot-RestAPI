package com.example.szssonjunyoung.api.szs.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
public class LoginReq {

    @Schema(description = "아이디", example = "", requiredMode = Schema.RequiredMode.REQUIRED)
    private String userId;

    @Schema(description = "패스워드", example = "", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}
