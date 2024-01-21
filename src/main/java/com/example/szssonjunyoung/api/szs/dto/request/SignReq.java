package com.example.szssonjunyoung.api.szs.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
public class SignReq {

    @Schema(description = "아이디", example = "test", requiredMode = Schema.RequiredMode.REQUIRED)
    private String userId;

    @Schema(description = "패스워드", example = "test", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    @Schema(description = "이름", example = "홍길동", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "주민등록번호", example = "860824-1655068", requiredMode = Schema.RequiredMode.REQUIRED)
    private String regNo;
}
