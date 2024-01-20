package com.example.szssonjunyoung.api.szs.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "refund")
public class RefundEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 결정세액
    private String determinedTaxAmount;

    // 퇴직연금세액공제
    private String retirementPensionTaxDeduction;

    @OneToOne
    @JoinColumn(name = "scrap_info_id", nullable = false)
    private ScrapInfoEntity scrapInfo;
}
