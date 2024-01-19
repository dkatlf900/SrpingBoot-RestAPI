package com.example.szssonjunyoung.api.szs.dto.response;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserInfoRes {
    @Schema(description = "pk", example = "")
    private Long id;

    @Schema(description = "아이디", example = "")
    private String userId;

    @Schema(description = "주민등록번호", example = "")
    private String regNo;

    @Schema(description = "이름", example = "")
    private String name;

}
