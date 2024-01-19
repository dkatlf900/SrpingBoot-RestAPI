package com.example.szssonjunyoung.api.szs.controller;


import com.example.szssonjunyoung.api.szs.dto.request.LoginReq;
import com.example.szssonjunyoung.api.szs.service.LoginService;
import com.example.szssonjunyoung.base.dto.GeneralResponse;
import com.example.szssonjunyoung.core.token.TokenInfoRes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "로그인 API", description = "")
@Slf4j
@RestController
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping("/szs/login")
    @Operation(summary = "로그인 API", description = "")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(schema = @Schema(implementation = TokenInfoRes.class)))
    })
    public GeneralResponse<TokenInfoRes> userLogin(@Valid @RequestBody LoginReq loginReq) {
        return new GeneralResponse<>(loginService.login(loginReq));
    }

}
