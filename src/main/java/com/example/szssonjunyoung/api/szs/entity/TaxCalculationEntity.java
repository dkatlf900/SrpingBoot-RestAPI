package com.example.szssonjunyoung.api.szs.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@NoArgsConstructor
@Table(name = "tax_calculation")
public class TaxCalculationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 산출세액
    private String total;

    @OneToOne
    @JoinColumn(name = "scrap_info_id", nullable = false)
    private ScrapInfoEntity scrapInfo;

}
