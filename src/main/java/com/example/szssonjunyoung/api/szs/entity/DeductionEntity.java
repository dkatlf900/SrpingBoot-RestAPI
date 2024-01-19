package com.example.szssonjunyoung.api.szs.entity;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "deduction")
public class DeductionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 금액
    private String amount;

    // 총납입금액
    private String totalPaymentAmount;

    // 소득구분
    private String amoutType;

    @ManyToOne
    @JoinColumn(name = "scrap_info_id", nullable = false)
    private ScrapInfoEntity scrapInfo;

}
