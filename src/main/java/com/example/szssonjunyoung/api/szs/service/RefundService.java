package com.example.szssonjunyoung.api.szs.service;


import com.example.szssonjunyoung.api.szs.dto.response.RefundRes;
import com.example.szssonjunyoung.api.szs.entity.*;
import com.example.szssonjunyoung.api.szs.repository.RefundRepository;
import com.example.szssonjunyoung.api.szs.repository.UserRepository;
import com.example.szssonjunyoung.core.aop.exception.ErrorCode;
import com.example.szssonjunyoung.core.aop.exception.custom.SzsException;
import com.example.szssonjunyoung.core.token.Account;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service("RefundService")
public class RefundService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefundRepository refundRepository;

    /**
     * 회원의 환급세액 Info
     */
    public RefundRes userRefundInfo(Account account) {
        Optional<UsersEntity> userOptional = userRepository.findById(Long.valueOf(account.getId()));
        if(userOptional.isPresent()) {
            UsersEntity user = userOptional.get();
            Optional<RefundEntity> refund = refundRepository.findByUserIdRefund(String.valueOf(user.getId()));
            if(!refund.isPresent()) {
                throw new SzsException(ErrorCode.ERROR_SZSE1006);
            }

            NumberFormat numberFormat = NumberFormat.getInstance();
            numberFormat.setGroupingUsed(true);
//            numberFormat.setRoundingMode(RoundingMode.DOWN); // 소수점 음.. 일단 그냥 보여주는걸로

            return RefundRes.builder()
                    .name(user.getName())
                    .determinedTaxAmount(numberFormat.format(refund.get().getDeterminedTaxAmount().doubleValue()))
                    .retirementPensionTaxDeduction(numberFormat.format(refund.get().getRetirementPensionTaxDeduction()))
                    .build();
        } else {
            throw new SzsException(ErrorCode.ERROR_SZSE1004);
        }
    }



    /**
     * 환급액 계산
     */
    public RefundEntity calculateRefund(ScrapInfoEntity scrapInfoEntity) {
        RefundEntity refundEntity = new RefundEntity();
        refundEntity.setScrapInfo(scrapInfoEntity);

        TaxCalculationEntity taxCalculationEntity = scrapInfoEntity.getTaxCalculation();

        // 산출세액
        BigDecimal determinedTaxAmount = new BigDecimal(taxCalculationEntity.getTotal().replace(",", ""));
        log.info("1.산출세액====> :{}",determinedTaxAmount);

        // 1. 근로소득세액공제금액
        // 근로소득세액공제금액 = 산출세액 * 0.55
        BigDecimal incomeTaxDeduction = determinedTaxAmount.multiply(new BigDecimal("0.55"));
        log.info("2.근로소득세액공제금액====> :{}",incomeTaxDeduction);

        // 2. 퇴직연금세액공제금액
        BigDecimal retirementPensionTaxDeduction = calculateRetirementPensionTaxDeduction(scrapInfoEntity);
        log.info("3.퇴직연금세액공제금액====> :{}",retirementPensionTaxDeduction);

        // 3. 특별세액공제금액
        BigDecimal specialDeductionAmount = calculateSpecialDeduction(scrapInfoEntity);
        log.info("4.특별세액공제금액====> :{}",specialDeductionAmount);

        // 4. 표준세액공제금액
        BigDecimal standardDeductionAmount = calculateStandardDeduction(specialDeductionAmount);
        log.info("5.표준세액공제금액====> :{}",standardDeductionAmount);


        // 결정세액 = 산출세액 - 근로소득세액공제금액 - 퇴직연금세액공제금액 - 특별세액공제금액 - 표준세액공제금액
        BigDecimal result = determinedTaxAmount.subtract(incomeTaxDeduction)
                .subtract(retirementPensionTaxDeduction)
                .subtract(specialDeductionAmount)
                .subtract(standardDeductionAmount);
        log.info("6.결정세액====> :{}",result);

        // 결정세액이 음수인 경우 0으로 처리
        result = result.max(BigDecimal.ZERO);

        refundEntity.setDeterminedTaxAmount(result);
        refundEntity.setRetirementPensionTaxDeduction(retirementPensionTaxDeduction);
        return refundEntity;
    }

    /**
     * 퇴직연금세액공제금액
     * 퇴직연금세액공제금액 = 퇴직연금 납입금액 * 0.15
     */
    private BigDecimal calculateRetirementPensionTaxDeduction(ScrapInfoEntity scrapInfoEntity) {
        BigDecimal totalPaymentAmount = scrapInfoEntity.getDeductionEntities().stream()
                .filter(deductionEntity -> "퇴직연금".equals(deductionEntity.getAmoutType()))
                .map(deductionEntity -> new BigDecimal(deductionEntity.getTotalPaymentAmount().replace(",", "")))
                .findFirst()
                .orElse(BigDecimal.ZERO);

        return totalPaymentAmount.multiply(new BigDecimal("0.15"));
    }


    /**
     * 특별세액공제금액
     * 특별세액공제금액 = 보험료 + 의료비 + 교육비 + 기부금
     */
    private BigDecimal calculateSpecialDeduction(ScrapInfoEntity scrapInfoEntity) {
        List<DeductionEntity> deductionEntities = scrapInfoEntity.getDeductionEntities();

        // 보험료, 의료비, 교육비, 기부금에 대한 특별세액공제 계산
        BigDecimal insuranceDeduction = calculateInsuranceDeduction(deductionEntities); // 보험료
        log.info("====> 보험료: {}", insuranceDeduction);
        BigDecimal medicalExpensesDeduction = calculateMedicalExpensesDeduction(scrapInfoEntity); // 의료비
        log.info("====> 의료비: {}", medicalExpensesDeduction);

        BigDecimal educationExpensesDeduction = calculateEducationExpensesDeduction(deductionEntities); // 교육비
        log.info("====> 교육비: {}", educationExpensesDeduction);

        BigDecimal donationDeduction = calculateDonationDeduction(deductionEntities); // 기부금
        log.info("====> 기부금: {}", donationDeduction);

        // 계산된 특별세액공제금액의 4개 합
        return insuranceDeduction
                .add(medicalExpensesDeduction)
                .add(educationExpensesDeduction)
                .add(donationDeduction);
    }



    /**
     * 표준세액공제금액을 계산하는 메서드
     */
    private BigDecimal calculateStandardDeduction(BigDecimal specialDeductionAmount) {
        // 표준세액공제금액: 특별세액공제금액의 합이 130,000원 미만일 경우 130,000원, 그 이상일 경우 0원
        return specialDeductionAmount.compareTo(new BigDecimal("130000")) < 0 ?
                new BigDecimal("130000") : BigDecimal.ZERO;
    }


    /**
     * 보험료공제금액을 계산하는 메서드
     * 보험료공제금액 = 보험료납입금액 * 12%
     */
    private BigDecimal calculateInsuranceDeduction(List<DeductionEntity> deductionEntities) {
        return deductionEntities.stream()
                .filter(deductionEntity -> "보험료".equals(deductionEntity.getAmoutType()))
                .map(deductionEntity -> new BigDecimal(deductionEntity.getAmount().replace(",", ""))
                        .multiply(new BigDecimal("0.12")))
                .findFirst()
                .orElse(BigDecimal.ZERO);
    }



    /**
     * 의료비공제금액
     * 의료비공제금액 = (의료비납입금액 - 총급여 * 3%) * 15%
     * 단, 의료비공제금액 < 0 일 경우, 의료비공제금액 = 0 처리 한다.
     */
    private BigDecimal calculateMedicalExpensesDeduction(ScrapInfoEntity scrapInfoEntity) {
        BigDecimal totalPayment = new BigDecimal(scrapInfoEntity.getSalarys().get(0).getTotalPaymentAmount().replace(",", ""));

        BigDecimal medicalExpenses = new BigDecimal(scrapInfoEntity.getDeductionEntities().stream()
                .filter(deductionEntity -> "의료비".equals(deductionEntity.getAmoutType()))
                .map(deductionEntity -> deductionEntity.getAmount().replace(",", ""))
                .findFirst()
                .orElse("0"));

        log.info("##### totalPayment :{}", totalPayment);
        log.info("##### medicalExpenses :{}", medicalExpenses);

        BigDecimal medicalExpensesDeduction = medicalExpenses.subtract(totalPayment.multiply(new BigDecimal("0.03")))
                .multiply(new BigDecimal("0.15"));

        // 의료비공제금액이 음수인 경우 0으로 처리
        return medicalExpensesDeduction.max(BigDecimal.ZERO);
    }

    /**
     * 교육비공제금액
     * 교육비공제금액 = 교육비납입금액 * 15%
     */
    private BigDecimal calculateEducationExpensesDeduction(List<DeductionEntity> deductionEntities) {
        return deductionEntities.stream()
                .filter(deductionEntity -> "교육비".equals(deductionEntity.getAmoutType()))
                .map(deductionEntity -> new BigDecimal(deductionEntity.getAmount().replace(",", ""))
                        .multiply(new BigDecimal("0.15")))
                .findFirst()
                .orElse(BigDecimal.ZERO);
    }

    /**
     * 기부금공제금액
     * 기부금공제금액 = 기부금납입금액 * 15%
     */
    private BigDecimal calculateDonationDeduction(List<DeductionEntity> deductionEntities) {
        return deductionEntities.stream()
                .filter(deductionEntity -> "기부금".equals(deductionEntity.getAmoutType()))
                .map(deductionEntity -> new BigDecimal(deductionEntity.getAmount().replace(",", ""))
                        .multiply(new BigDecimal("0.15")))
                .findFirst()
                .orElse(BigDecimal.ZERO);
    }
}
