package com.example.szssonjunyoung.api.szs.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "salary")
public class SalaryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 소득내역
    private String incomeType;

    // 총지급액
    private String totalPaymentAmount;

    // 업무시작일
    private String workStartDate;

    // 기업명
    private String companyName;

    //이름
    private String name;

    // 지급일
    private String paymentDate;

    // 업무종료일
    private String workEndDate;

    // 주민등록번호
    private String residentRegistrationNumber;

    // 소득구분
    private String incomeCategory;

    // 사업자등록번호
    private String businessRegistrationNumber;

    @ManyToOne
    @JoinColumn(name = "scrap_info_id", nullable = false)
    private ScrapInfoEntity scrapInfo;
}
