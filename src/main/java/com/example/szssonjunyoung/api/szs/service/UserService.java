package com.example.szssonjunyoung.api.szs.service;

import com.example.szssonjunyoung.api.szs.dto.response.SzsScrapRes;
import com.example.szssonjunyoung.api.szs.dto.response.UserInfoRes;
import com.example.szssonjunyoung.api.szs.entity.UsersEntity;
import com.example.szssonjunyoung.api.szs.repository.UserRepository;
import com.example.szssonjunyoung.base.util.AES256Util;
import com.example.szssonjunyoung.core.token.Account;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service("UserService")
public class UserService {

    @Autowired
    private UserRepository userRepository;

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


    public SzsScrapRes userInfoScrap(Account account) {
        Optional<UsersEntity> user = userRepository.findById(Long.valueOf(account.getId()));
        if(user.isPresent()) {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("name", user.get().getName());
            requestBody.put("regNo", AES256Util.decryptAES(user.get().getRegNo()));

            // TODO 일단 필요한 정보들을 Db에 저장해야 한다.
            SzsScrapRes response = webClient.post()
                    .uri(scrapUrl)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(SzsScrapRes.class)
                    .timeout(Duration.ofSeconds(40)) // 최대 40초 대기
                    .retryWhen(Retry.backoff(1, Duration.ofSeconds(1)))
                    .doOnError(e -> {
                        // 에러 처리 로직 추가
//                        e.printStackTrace();
                    })
                    .block();
            return response;
        }
        return null;
    }
}
