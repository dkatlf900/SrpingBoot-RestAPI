package com.example.szssonjunyoung.core.token;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class TokenInfoRes {

    @Schema(description = "JWT 토큰 타입", example = "Bearer")
    private String grantType;

    @Schema(description = "jwt 토큰값", example = "")
    private String jwtToken;
}
