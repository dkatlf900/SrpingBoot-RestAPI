package com.example.szssonjunyoung.api.szs.controller;

import com.example.szssonjunyoung.api.szs.dto.request.SignReq;
import com.example.szssonjunyoung.api.szs.service.SignService;
import com.example.szssonjunyoung.base.dto.GeneralResponse;
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

@Tag(name = "1.회원가입 API", description = "")
@Slf4j
@RestController
public class SignController {

    @Autowired
    private SignService signService;


    @PostMapping("/szs/signup")
    @Operation(summary = "회원가입 API", description = "")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(schema = @Schema(implementation = Boolean.class)))
    })
    public GeneralResponse<Boolean> userSign(@Valid @RequestBody SignReq signReq) {
        Boolean isSuccess = signService.signUser(signReq);
        return new GeneralResponse<>(isSuccess);
    }
}
