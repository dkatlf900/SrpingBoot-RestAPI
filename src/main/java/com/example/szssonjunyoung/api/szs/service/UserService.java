package com.example.szssonjunyoung.api.szs.service;

import com.example.szssonjunyoung.api.szs.dto.response.SzsScrapRes;
import com.example.szssonjunyoung.api.szs.dto.response.UserInfoRes;
import com.example.szssonjunyoung.api.szs.entity.*;
import com.example.szssonjunyoung.api.szs.repository.ScrapInfoRepository;
import com.example.szssonjunyoung.api.szs.repository.UserRepository;
import com.example.szssonjunyoung.base.util.AES256Util;
import com.example.szssonjunyoung.core.aop.exception.ErrorCode;
import com.example.szssonjunyoung.core.aop.exception.custom.SzsException;
import com.example.szssonjunyoung.core.token.Account;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

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
    private RefundService refundService;

    @Autowired
    private WebClient webClient;

    @Value("${szs.scrap-url}")
    private String scrapUrl;


    public UserInfoRes userInfo(Account account) {
        // 토큰에 해당하는 회원정보 조회
        Optional<UsersEntity> user = userRepository.findById(Long.valueOf(account.getId()));
        if(user.isPresent()) {
            return UserInfoRes.builder()
                    .id(user.get().getId())
                    .userId(user.get().getUserId())
                    .name(user.get().getName())
                    .regNo(AES256Util.decryptAES(user.get().getRegNo()))
                    .build();
        } else {
            throw new SzsException(ErrorCode.ERROR_SZSE1004);
        }
    }


    /**
     * 회원정보 스크랩API 호출 및 환급금액 계산
     */
    @Transactional
    public SzsScrapRes userInfoScrap(Account account) {
        Optional<UsersEntity> userOptional = userRepository.findById(Long.valueOf(account.getId()));
        if(userOptional.isPresent()) {
            UsersEntity user = userOptional.get();

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("name", user.getName());
            requestBody.put("regNo", AES256Util.decryptAES(user.getRegNo()));

            // 스크랩 Info 호출 및 저장
            SzsScrapRes response = webClient.post()
                    .uri(scrapUrl)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(SzsScrapRes.class)
                    .timeout(Duration.ofSeconds(30)) // 최대 30초 대기
                    .retryWhen(Retry.backoff(1, Duration.ofSeconds(1))) // 실패시 backoff 1회 재호출
                    .doOnError(e -> {
                        // 에러 처리 로직 추가
//                        e.printStackTrace();
                        throw new SzsException(ErrorCode.ERROR_SZSE1005);
                    })
                    .block();

            if (response != null) {
                saveScrapInfo(user, response);
            }
            return response;
        } else {
            throw new SzsException(ErrorCode.ERROR_SZSE1004);
        }
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
        RefundEntity refundEntity = refundService.calculateRefund(scrapInfoEntity);
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

}
