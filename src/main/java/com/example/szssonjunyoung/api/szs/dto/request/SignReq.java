package com.example.szssonjunyoung.api.szs.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
public class SignReq {

    @Schema(description = "아이디", example = "")
    private String userId;

    @Schema(description = "패스워드", example = "")
    private String password;

    @Schema(description = "이름", example = "")
    private String name;

    @Schema(description = "주민등록번호", example = "")
    private String regNo;
}
