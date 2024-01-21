package com.example.szssonjunyoung.api.szs.controller;


import com.example.szssonjunyoung.api.szs.dto.response.RefundRes;
import com.example.szssonjunyoung.api.szs.service.RefundService;
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
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "4.환급조회 API", description = "")
@Slf4j
@RestController
public class RefundController {

    @Autowired
    private RefundService refundService;

    @GetMapping("/szs/refund")
    @Operation(summary = "My 환급금액 API | (jwt token필수)", description = "")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "SUCCESS", content = @Content(schema = @Schema(implementation = RefundRes.class)))
    })
    public GeneralResponse<RefundRes> userRefund(@AuthenticationPrincipal Account account) {
        return new GeneralResponse<>(refundService.userRefundInfo(account));
    }
}
