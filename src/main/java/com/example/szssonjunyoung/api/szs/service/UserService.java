package com.example.szssonjunyoung.api.szs.service;

import com.example.szssonjunyoung.api.szs.dto.response.SzsScrapRes;
import com.example.szssonjunyoung.api.szs.dto.response.UserInfoRes;
import com.example.szssonjunyoung.api.szs.entity.*;
import com.example.szssonjunyoung.api.szs.repository.ScrapInfoRepository;
import com.example.szssonjunyoung.api.szs.repository.UserRepository;
import com.example.szssonjunyoung.base.util.AES256Util;
import com.example.szssonjunyoung.core.token.Account;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service("UserService")
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ScrapInfoRepository scrapInfoRepository;

    @Autowired
    private WebClient webClient;

    @Value("${szs.scrap-url}")
    private String scrapUrl;


    public UserInfoRes userInfo(Account account) {
        // 여기서 Pk로 DB조회하여 정보 담아서 보내주기만 하면된다.
        Optional<UsersEntity> user = userRepository.findById(Long.valueOf(account.getId()));
        if(user.isPresent()) {
            return UserInfoRes.builder()
                    .id(user.get().getId())
                    .userId(user.get().getUserId())
                    .name(user.get().getName())
                    .regNo(AES256Util.decryptAES(user.get().getRegNo()))
                    .build();
        } else {
            // TODO 만약 혹시나 회원이 없다면?
            return null;
        }
    }


    @Transactional
    public SzsScrapRes userInfoScrap(Account account) {
        Optional<UsersEntity> userOptional = userRepository.findById(Long.valueOf(account.getId()));
        if(userOptional.isPresent()) {
            UsersEntity user = userOptional.get();

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("name", user.getName());
            requestBody.put("regNo", AES256Util.decryptAES(user.getRegNo()));

            // TODO 일단 필요한 정보들을 Db에 저장해야 한다. 또한 장애발생시 처리도 필요.
            SzsScrapRes response = webClient.post()
                    .uri(scrapUrl)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(SzsScrapRes.class)
                    .timeout(Duration.ofSeconds(40)) // 최대 40초 대기
                    .retryWhen(Retry.backoff(1, Duration.ofSeconds(1))) // 실패시 backoff 1회 재호출
                    .doOnError(e -> {
                        // 에러 처리 로직 추가
//                        e.printStackTrace();
                    })
                    .block();
            // TODO response 로 데이터 저장해야한다.
            if (response != null) {
                saveScrapInfo(user, response);
            }

            return response;
        }
        return null;
    }


    private void saveScrapInfo(UsersEntity user, SzsScrapRes response) {
        ScrapInfoEntity scrapInfoEntity = new ScrapInfoEntity();
        // ScrapInfoEntity에 필요한 정보 세팅
        scrapInfoEntity.setUser(user);
        scrapInfoEntity.setAppVer(response.getData().getAppVer());
        scrapInfoEntity.setErrMsg(response.getData().getErrMsg());
        scrapInfoEntity.setCompany(response.getData().getCompany());
        scrapInfoEntity.setSvcCd(response.getData().getSvcCd());
        scrapInfoEntity.setHostNm(response.getData().getHostNm());
        scrapInfoEntity.setWorkerResDt(response.getData().getWorkerResDt());
        scrapInfoEntity.setWorkerReqDt(response.getData().getWorkerReqDt());
        // ScrapInfoEntity를 먼저 저장
        scrapInfoRepository.save(scrapInfoEntity);

        // SalaryEntity 저장
        List<SalaryEntity> salaryEntities = response.getData().getJsonList().getSalaryInfos().stream()
                .map(salaryInfo -> mapToSalaryEntity(salaryInfo, scrapInfoEntity))
                .collect(Collectors.toList());
        scrapInfoEntity.setSalarys(salaryEntities);

        // TaxCalculationEntity 저장
        TaxCalculationEntity taxCalculationEntity = mapToTaxCalculationEntity(response.getData().getJsonList(), scrapInfoEntity);
        scrapInfoEntity.setTaxCalculation(taxCalculationEntity);

        // DeductionEntity 저장
        List<DeductionEntity> deductionEntities = response.getData().getJsonList().getDeductionInfos().stream()
                .map(salaryInfo -> mapToDeductionEntity(salaryInfo, scrapInfoEntity))
                .collect(Collectors.toList());
        scrapInfoEntity.setDeductionEntities(deductionEntities);


        // RefundEntity 저장
        RefundEntity refundEntity = calculateRefund(scrapInfoEntity);
        scrapInfoEntity.setRefund(refundEntity);
    }


    /**
     * 급여정보 info
     */
    private SalaryEntity mapToSalaryEntity(SzsScrapRes.SzsScrapData.SzsScrapJsonList.SalaryInfo salaryInfo, ScrapInfoEntity scrapInfoEntity) {
            return SalaryEntity.builder()
                .scrapInfo(scrapInfoEntity)
                .incomeType(salaryInfo.getIncomeType())
                .incomeCategory(salaryInfo.getIncomeCategory())
                .totalPaymentAmount(salaryInfo.getTotalPaymentAmount())
                .workStartDate(salaryInfo.getWorkStartDate())
                .workEndDate(salaryInfo.getWorkEndDate())
                .companyName(salaryInfo.getCompanyName())
                .name(salaryInfo.getName())
                .paymentDate(salaryInfo.getPaymentDate())
                .residentRegistrationNumber(salaryInfo.getResidentRegistrationNumber())
                .businessRegistrationNumber(salaryInfo.getBusinessRegistrationNumber())
                .build();
    }

    /**
     * 소득공제 info
     */
    private DeductionEntity mapToDeductionEntity(SzsScrapRes.SzsScrapData.SzsScrapJsonList.DeductionInfo deductionInfo, ScrapInfoEntity scrapInfoEntity) {
        return DeductionEntity.builder()
                .scrapInfo(scrapInfoEntity)
                .amount(deductionInfo.getAmount() != null ? deductionInfo.getAmount() : null)
                .totalPaymentAmount(deductionInfo.getTotalPaymentAmount() != null ? deductionInfo.getTotalPaymentAmount() : null)
                .amoutType(deductionInfo.getAmoutType())
                .build();
    }


    /**
     * 산출세액 info
     */
    private TaxCalculationEntity mapToTaxCalculationEntity(SzsScrapRes.SzsScrapData.SzsScrapJsonList jsonList, ScrapInfoEntity scrapInfoEntity) {
        return TaxCalculationEntity.builder()
                .scrapInfo(scrapInfoEntity)
                .total(jsonList.getTotal())
                .build();
    }


    /**
     * 결정세액 info
     */
    private RefundEntity calculateRefund(ScrapInfoEntity scrapInfoEntity) {
        RefundEntity refundEntity = new RefundEntity();
        TaxCalculationEntity taxCalculationEntity = scrapInfoEntity.getTaxCalculation();

        // 산출세액
        BigDecimal determinedTaxAmount = new BigDecimal(taxCalculationEntity.getTotal().replace(",", ""));

        // 1. 근로소득세액공제금액
        BigDecimal incomeTaxDeduction = determinedTaxAmount.multiply(new BigDecimal("0.55"));

        // 2. 퇴직연금세액공제금액
        BigDecimal retirementPensionTaxDeduction = calculateRetirementPensionTaxDeduction(scrapInfoEntity);

        // 3. 특별세액공제금액
        BigDecimal specialDeductionAmount = calculateSpecialDeduction(scrapInfoEntity);

        // 4. 표준세액공제금액
        BigDecimal standardDeductionAmount = calculateStandardDeduction(specialDeductionAmount);


        // 결정세액 계산: 결정세액 = 산출세액 - 근로소득세액공제금액 - 퇴직연금세액공제금액 - 특별세액공제금액 - 표준세액공제금액
        BigDecimal result = determinedTaxAmount.subtract(incomeTaxDeduction)
                .subtract(retirementPensionTaxDeduction)
                .subtract(specialDeductionAmount)
                .subtract(standardDeductionAmount);

        // 결정세액이 음수인 경우 0으로 처리
        result = result.max(BigDecimal.ZERO);

        // 계산 결과를 엔티티에 저장
        refundEntity.setDeterminedTaxAmount(result.toString());
        refundEntity.setRetirementPensionTaxDeduction(retirementPensionTaxDeduction.toString());

        // RefundEntity를 저장
        return refundEntity;
    }

    private BigDecimal calculateRetirementPensionTaxDeduction(ScrapInfoEntity scrapInfoEntity) {
        // 퇴직연금세액공제금액 = 퇴직연금 납입금액 * 0.15
        BigDecimal totalPaymentAmount = scrapInfoEntity.getDeductionEntities().stream()
                .filter(deductionEntity -> "퇴직연금".equals(deductionEntity.getAmoutType()))
                .map(deductionEntity -> new BigDecimal(deductionEntity.getTotalPaymentAmount().replace(",", "")))
                .findFirst()
                .orElse(BigDecimal.ZERO);

        return totalPaymentAmount.multiply(new BigDecimal("0.15"));
    }


    /**
     * 특별세액공제금액
     */
    private BigDecimal calculateSpecialDeduction(ScrapInfoEntity scrapInfoEntity) {
        List<DeductionEntity> deductionEntities = scrapInfoEntity.getDeductionEntities();

        // 보험료, 의료비, 교육비, 기부금에 대한 특별세액공제 계산
        BigDecimal insuranceDeduction = calculateInsuranceDeduction(deductionEntities); // 보험료
        BigDecimal medicalExpensesDeduction = calculateMedicalExpensesDeduction(scrapInfoEntity); // 의료비
        BigDecimal educationExpensesDeduction = calculateEducationExpensesDeduction(deductionEntities); // 교육비
        BigDecimal donationDeduction = calculateDonationDeduction(deductionEntities); // 기부금

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
     */
    private BigDecimal calculateInsuranceDeduction(List<DeductionEntity> deductionEntities) {
        // 보험료공제금액 = 보험료납입금액 * 12%
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

        BigDecimal medicalExpensesDeduction = medicalExpenses.subtract(totalPayment.multiply(new BigDecimal("0.03")))
                .multiply(new BigDecimal("0.15"));

        // 의료비공제금액이 음수인 경우 0으로 처리
        return medicalExpensesDeduction.max(BigDecimal.ZERO);
    }

    /**
     * 교육비공제금액
     */
    private BigDecimal calculateEducationExpensesDeduction(List<DeductionEntity> deductionEntities) {
        // 교육비공제금액 = 교육비납입금액 * 15%
        return deductionEntities.stream()
                .filter(deductionEntity -> "교육비".equals(deductionEntity.getAmoutType()))
                .map(deductionEntity -> new BigDecimal(deductionEntity.getAmount().replace(",", ""))
                        .multiply(new BigDecimal("0.15")))
                .findFirst()
                .orElse(BigDecimal.ZERO);
    }

    /**
     * 기부금공제금액
     */
    private BigDecimal calculateDonationDeduction(List<DeductionEntity> deductionEntities) {
        // 기부금공제금액 = 기부금납입금액 * 15%
        return deductionEntities.stream()
                .filter(deductionEntity -> "기부금".equals(deductionEntity.getAmoutType()))
                .map(deductionEntity -> new BigDecimal(deductionEntity.getAmount().replace(",", ""))
                        .multiply(new BigDecimal("0.15")))
                .findFirst()
                .orElse(BigDecimal.ZERO);
    }


}
