package com.example.szssonjunyoung.api.szs.controller;


import com.example.szssonjunyoung.api.szs.dto.response.SzsScrapRes;
import com.example.szssonjunyoung.api.szs.dto.response.UserInfoRes;
import com.example.szssonjunyoung.api.szs.service.UserService;
import com.example.szssonjunyoung.base.dto.GeneralResponse;
import com.example.szssonjunyoung.core.token.Account;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "3.회원정보 | 스크랩 API", description = "")
@Slf4j
@RestController
public class UserInfoController {

    @Autowired
    private UserService userService;

    @GetMapping("/szs/me")
    @Operation(summary = "1.My 회원정보 API | (jwt token필수)" , description = "")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(schema = @Schema(implementation = UserInfoRes.class)))
    })
    public GeneralResponse<UserInfoRes> userInfo(@AuthenticationPrincipal Account account) {
        return new GeneralResponse<>(userService.userInfo(account));
    }


    @PostMapping("/szs/scrap")
    @Operation(summary = "2.My 회원정보 Scrap API | (jwt token필수)", description = "")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(schema = @Schema(implementation = SzsScrapRes.class)))
    })
    public GeneralResponse<SzsScrapRes> userInfoScrap(@AuthenticationPrincipal Account account) {
        return new GeneralResponse<>(userService.userInfoScrap(account));
    }


}
