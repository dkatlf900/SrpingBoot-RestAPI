package com.example.szssonjunyoung.api.szs.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class SzsScrapRes {

    private String status;
    private SzsScrapData data;
    private Errors errors;

    @Data
    public class SzsScrapData {

        private SzsScrapJsonList jsonList;
        private String appVer;
        private String errMsg;
        private String company;
        private String svcCd;
        private String hostNm;
        private String workerResDt;
        private String workerReqDt;


        @Data
        public static class SzsScrapJsonList {

            @JsonProperty("급여")
            private List<SalaryInfo> salaryInfos;

            @JsonProperty("산출세액")
            private String total;

            @JsonProperty("소득공제")
            private List<DeductionInfo> deductionInfos;


            @Data
            public static class SalaryInfo {
                @JsonProperty("소득내역")
                private String incomeType;

                @JsonProperty("총지급액")
                private String totalPaymentAmount;

                @JsonProperty("업무시작일")
                private String workStartDate;

                @JsonProperty("기업명")
                private String companyName;

                @JsonProperty("이름")
                private String name;

                @JsonProperty("지급일")
                private String paymentDate;

                @JsonProperty("업무종료일")
                private String workEndDate;

                @JsonProperty("주민등록번호")
                private String residentRegistrationNumber;

                @JsonProperty("소득구분")
                private String incomeCategory;

                @JsonProperty("사업자등록번호")
                private String businessRegistrationNumber;
            }

            @Data
            public static class DeductionInfo {
                @JsonProperty("금액")
                @JsonInclude(JsonInclude.Include.NON_NULL)
                private String amount;

                @JsonProperty("총납입금액")
                @JsonInclude(JsonInclude.Include.NON_NULL)
                private String totalPaymentAmount;

                @JsonProperty("소득구분")
                private String amoutType;
            }
        }
    }

    @Data
    public static class Errors {
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String msg;
    }
}
