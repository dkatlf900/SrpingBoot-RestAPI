package com.example.szssonjunyoung.api.szs.entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Entity
@Data
@NoArgsConstructor
@Table(name = "deduction")
public class DeductionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 금액
    @Column(nullable = true)
    private String amount;

    // 총납입금액
    @Column(nullable = true)
    private String totalPaymentAmount;

    // 소득구분
    @Column(nullable = false)
    private String amoutType;

    @ManyToOne
    @JoinColumn(name = "scrap_info_id", nullable = false)
    private ScrapInfoEntity scrapInfo;

}
