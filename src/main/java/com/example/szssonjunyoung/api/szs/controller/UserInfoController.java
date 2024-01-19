package com.example.szssonjunyoung.api.szs.controller;


import com.example.szssonjunyoung.api.szs.dto.request.LoginReq;
import com.example.szssonjunyoung.api.szs.dto.response.UserInfoRes;
import com.example.szssonjunyoung.base.dto.GeneralResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "회원정보 API", description = "")
@Slf4j
@RestController
public class UserInfoController {

    @GetMapping("/szs/me")
    @Operation(summary = "회원정보 API", description = "")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(schema = @Schema(implementation = UserInfoRes.class)))
    })
    public GeneralResponse<UserInfoRes> userInfo(@Valid @RequestBody LoginReq loginReq) {
        return new GeneralResponse<>();
    }
}
