package com.example.szssonjunyoung.core.token;

import io.jsonwebtoken.Claims;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {

    private String id; // 회원 pk
    private String name; // 회원 이름
    private String userId; // 회원 로그인 ID

    private Collection<? extends GrantedAuthority> authorities; // 권한


    public Account(Claims claims, Collection<? extends GrantedAuthority> authorities) {
        this.id = (String) claims.getOrDefault("sub", "");
        this.name = (String) claims.getOrDefault("name", "");
        this.userId = (String) claims.getOrDefault("userId", "");
        this.authorities = authorities;
    }


}
