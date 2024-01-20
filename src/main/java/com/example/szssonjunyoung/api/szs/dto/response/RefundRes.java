package com.example.szssonjunyoung.api.szs.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class RefundRes {

    @JsonProperty("이름")
    @Schema(description = "이름", example = "")
    private String name;

    @JsonProperty("결정세액")
    @Schema(description = "결정세액", example = "")
    private String determinedTaxAmount;

    @JsonProperty("퇴직연금세액공제")
    @Schema(description = "퇴직연금세액공제", example = "")
    private String retirementPensionTaxDeduction;
}
