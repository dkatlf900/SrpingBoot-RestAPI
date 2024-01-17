package com.example.szssonjunyoung.base.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiResult<T> {

    final public static String RESULT_CODE_OK = "200";
    final public static String OK = "OK";

    @Schema(description = "결과 코드", example = "200 or Error Code")
    private String code;

    @Schema(description = "결과 메시지", example = "OK or Error message")
    private String msg;

    public ApiResult(String code) {
        this.code = code;
    }

    public ApiResult(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ApiResult() {
        this.code = RESULT_CODE_OK;
        this.msg = OK;
    }
}
