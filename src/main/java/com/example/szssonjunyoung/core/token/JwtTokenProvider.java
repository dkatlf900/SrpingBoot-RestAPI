package com.example.szssonjunyoung.core.token;

import com.example.szssonjunyoung.api.szs.entity.UsersEntity;
import com.example.szssonjunyoung.core.aop.exception.ErrorCode;
import com.example.szssonjunyoung.core.aop.exception.custom.SzsException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {

    private final Key key;

    private final Long accessTokenExpire;



    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey, @Value("${jwt.expire}") Long expire) {
        byte[] keyBytes = secretKey.getBytes();
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenExpire = expire;
    }


    /**
     * 로그인 성공시 토큰 발급
     */
    public TokenInfoRes createLoginToken(UsersEntity usersEntity) {
        Claims claims = Jwts.claims().setSubject(String.valueOf(usersEntity.getId()));
        claims.put("name", usersEntity.getName());
        claims.put("userId", usersEntity.getUserId());
        claims.put("roles", "ROLE_USER");

        String jwtToken = this.createToken(claims);
        return TokenInfoRes.builder()
                .grantType("Bearer")
                .jwtToken(jwtToken)
                .build();
    }

    /**
     * create jwt
     */
    private String createToken(Claims claims) {
        Date now = new Date();

        String token = Jwts.builder()
            .setClaims(claims)
            .setIssuer("szs")
            .setIssuedAt(now) // 토큰 발행 시간 정보
            .setExpiration(new Date(now.getTime() + (accessTokenExpire * 1000))) // 만료 Expire Time
            .signWith(this.key, SignatureAlgorithm.HS256)  // 사용할 암호화 알고리즘과 signature 에 들어갈 secret값 세팅
            .compact();
        return token;
    }

    /**
     * client id, 데이터 추출
     */
    public Claims getClientClaims(String token) {
        Claims claims = this.getClaims(token);
        return claims;
    }


    /**
     * 공통, getClaims
     */
    private Claims getClaims(String token) {
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(this.key)
                .requireIssuer("szs")
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims;
    }


    /**
     * 토큰의 유효성 + 만료일자 확인
     */
    public boolean validateToken(String jwtToken) {
        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(this.key)
                    .requireIssuer("szs")
                    .build()
                    .parseClaimsJws(jwtToken);
            return !claimsJws.getBody().getExpiration().before(new Date());
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.debug(e.toString());
//            e.printStackTrace();
            throw new SzsException(ErrorCode.ERROR_SZSE1008);
        } catch (ExpiredJwtException e) {
            // 토큰 유효시간 만료
            throw new SzsException(ErrorCode.ERROR_SZSE1007);
        } catch (UnsupportedJwtException e) {
//            log.debug(e.toString());
            throw new SzsException(ErrorCode.ERROR_SZSE1008);
        } catch (IllegalArgumentException e) {
//            log.debug(e.toString());
            throw new SzsException(ErrorCode.ERROR_SZSE1008);
        }
//        return false;
    }


    public Authentication getAuthentication(String token) {
        Claims claims = this.getClaims(token);

        Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get("roles").toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        return new UsernamePasswordAuthenticationToken(new Account(claims, authorities), token, authorities);
    }

}