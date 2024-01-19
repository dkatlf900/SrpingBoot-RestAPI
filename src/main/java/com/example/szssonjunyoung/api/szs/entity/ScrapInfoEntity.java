package com.example.szssonjunyoung.api.szs.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@Table(name = "scrapInfo")
public class ScrapInfoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scrapInfoId;

//    @OneToMany
    @OneToMany(mappedBy = "scrapInfo", cascade = CascadeType.ALL)
    private List<SalaryEntity> salarys;

    @OneToOne(mappedBy = "scrapInfo", cascade = CascadeType.ALL)
    private TaxCalculationEntity taxCalculation;

    @OneToOne
    @JoinColumn(name = "id", nullable = false, unique = true)
    private UsersEntity user;

    // 스크랩 정보의 필드 추가
    private String appVer;
    private String errMsg;
    private String company;
    private String svcCd;
    private String hostNm;
    private String workerResDt;
    private String workerReqDt;
}
