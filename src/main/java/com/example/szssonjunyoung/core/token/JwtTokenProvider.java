package com.example.szssonjunyoung.core.token;

import com.example.szssonjunyoung.api.szs.entity.UsersEntity;
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
            e.printStackTrace();
//            throw new FantooException(ErrorCode.ERROR_AE5103);
        } catch (ExpiredJwtException e) {
            log.debug(e.toString());
//            throw new FantooException(ErrorCode.ERROR_AE5102);
        } catch (UnsupportedJwtException e) {
            log.debug(e.toString());
//            throw new FantooException(ErrorCode.ERROR_AE5103);
        } catch (IllegalArgumentException e) {
            log.debug(e.toString());
//            throw new FantooException(ErrorCode.ERROR_AE5103);

        }
        // TODO 여기 에러처리 해야하는데 일단 리턴으로하고 나중에 삭제해야한다
        return false;
    }






    /**
     * JWT Short-Term 토큰 생성 / (SNS 전용)
     * OAuth2 전용 inner process key
     */
   /* public String createShortTermToken(Authentication authentication) {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Claims claims = Jwts.claims().setSubject(String.valueOf(oAuth2User.getAttributes().get("integUid")));
        claims.put("integUid", oAuth2User.getAttributes().get("integUid"));
        claims.put("loginType", oAuth2User.getAttributes().get("loginType"));
        claims.put("roles", "ROLE_ACCOUNT");

        String shortTermToken = this.createToken(claims, key, shortTermTokenValidTime);
        return shortTermToken;
    }*/


    /**
     * JWT Short-Term 토큰 생성 / (Email | Phone | Apple / Twitter / google / facebook / kakao / line 사용중)
     * OAuth2 전용 inner process key
     */
    /*public String createShortTermToken(String keyName, LoginType loginType, String integUid) {
        Claims claims = Jwts.claims().setSubject(keyName);
        if (EnumSet.of(LoginType.apple, LoginType.twitter, LoginType.facebook, LoginType.google, LoginType.kakao, LoginType.line).contains(loginType)) {
            claims.put("integUid", integUid);
        } else {
            claims.put("integUid", "");
        }
        claims.put("loginType", loginType);
        claims.put("roles", "ROLE_ACCOUNT");

        String shortTermToken = this.createToken(claims, key, shortTermTokenValidTime);
        return shortTermToken;
    }*/


    /**
     * JWT Short-Term 토큰 생성 / (Client Login 인증 전용)
     * OAuth2 전용 inner process key
     */
    /*public String createClientIdToken(Oauth2Client oauth2Client, String redirectUri) {
        Claims claims = Jwts.claims();
        claims.put("clientId", oauth2Client.getClientId());
        claims.put("redirectUri", redirectUri);
        claims.put("roles", "ROLE_ACCOUNT");

        String shortTermToken = this.createToken(claims, key, accessClientIdTokenValidTime);
        return shortTermToken;
    }
*/



    /**
     * JWT 토큰 생성, Security provider를 통해 Login 요청시 발급.
     * AccessToken / RefreshToken
     */
    /*public TokenInfoRes createToken(Authentication authentication, String loginType) {
        String accessToken = null;
        String refreshToken = null;
        String authorities = null;
        User user = null;

        authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        if("sns".equals(loginType)) {
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
            user = User.builder()
                    .integUid((String) oAuth2User.getAttributes().get("integUid"))
                    .loginType((LoginType) oAuth2User.getAttributes().get("loginType"))
                    .build();
        } else if("email".equals(loginType) || "phone".equals(loginType)) {
            CustomDetails oAuth2User = (CustomDetails) authentication.getPrincipal();
            user = oAuth2User.getUser();
        }

        accessToken = createAccessToken(user, authorities);
        refreshToken = createRefreshToken(user);
        this.setToken(accessToken, refreshToken, user.getIntegUid());

        return TokenInfoRes.builder()
                .grantType("Bearer") // 사용안함
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

*/
    /**
     * JWT 토큰 생성
     * 신규 회원가입 회원에게 발급한다.
     * AccessToken / RefreshToken
     */
    /*public TokenInfoRes createJoinSuccessToken(String integUid, LoginType loginType) {
        User user = User.builder()
                .integUid(integUid)
                .loginType(loginType)
                .build();

        String accessToken = createAccessToken(user, "ROLE_" + AccountRole.USER);
        String refreshToken = createRefreshToken(user);
        this.setToken(accessToken, refreshToken, user.getIntegUid());

        return TokenInfoRes.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }*/


    /**
     *  로그인 성공시, 기존 security 사용못하는 provider가 사용한다.
     */
    /*public TokenInfoRes createSuccessToken(User user) {
        String accessToken = createAccessToken(user, user.getRoles());
        String refreshToken = createRefreshToken(user);
        this.setToken(accessToken, refreshToken, user.getIntegUid());

        return TokenInfoRes.builder()
                .grantType("Bearer")  // 사용안함
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }*/


    /**
     * AccessToken 재발급
     */
    /*@Transactional
    public String createReissueAccessToken(String reqAccessToken) {
        try {
            String getRefreshToken = valueOperations.get(RedisKeyType.userRefreshToken.getType() + reqAccessToken);

            if(ObjectUtils.isEmpty(getRefreshToken)) {
                throw new FantooException(ErrorCode.ERROR_AE5103);
            }

            // 회원정보
            String integUid = this.getAccessSubInfo(reqAccessToken);
            User user = userInfoService.isIntegUidUser(integUid);

            Claims claims = Jwts.claims().setSubject(String.valueOf(user.getIntegUid()));
            claims.put("roles", user.getRoles());
            claims.put("integUid", user.getIntegUid());
            claims.put("loginType", user.getLoginType());

            String accessToken = this.createToken(claims, key, accessTokenExpire);

            // last_login_date update
            userInfoService.updateLastLogin(integUid);

            // token 갱신
            this.setReissueToken(reqAccessToken, accessToken, getRefreshToken);
            redisService.redisDelete(RedisKeyType.userRefreshToken.getType() + reqAccessToken);

            return accessToken;
        } catch (FantooException e) {
            throw new FantooException(ErrorCode.ERROR_AE5103);
        }
    }
*/


    /**
     * AccessToken 발급
     */
    /*private String createAccessToken(User user, String authorities) {
        Claims claims = Jwts.claims().setSubject(String.valueOf(user.getIntegUid()));
        claims.put("roles", authorities);
        claims.put("integUid", user.getIntegUid());
        claims.put("loginType", user.getLoginType());

        String accessToken = this.createToken(claims, key, accessTokenExpire);
        return accessToken;
    }
*/


    /**
     * RefreshToken 발급
     */
   /* private String createRefreshToken(User user) {
        Claims claims = Jwts.claims().setSubject(String.valueOf(user.getIntegUid()));

        String refreshToken = this.createToken(claims, key, RedisKeyType.userRefreshToken.getExpireSeconds());
        return refreshToken;
    }

*/



    /**
     * access_token, sub값 integUid 추출
     */
    /*private String getAccessSubInfo(String token) {
        try {
            final String payloadJWT = token.split("\\.")[1];
            Base64.Decoder decoder = Base64.getUrlDecoder();

            final String payload = new String(decoder.decode(payloadJWT));
            JsonParser jsonParser = new BasicJsonParser();
            Map<String, Object> jsonArray = jsonParser.parseMap(payload);

            if (!jsonArray.containsKey("sub")) {
                throw new FantooException(ErrorCode.ERROR_AE5103);
            }
            return jsonArray.get("sub").toString();
        } catch (Exception e) {
            throw new FantooException(ErrorCode.ERROR_AE5103);
        }
    }*/


    /**
     * 공통, getClaims
     */
   /* private Claims getClaims(String token, Key key) {
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(key)
                .requireIssuer("fantoo")
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims;
    }*/



    /**
     * client id, 데이터 추출
     */
   /* public Claims getClientClaims(String token) {
        Claims claims = this.getClaims(token, key);
        return claims;
    }*/



    public Authentication getAuthentication(String token) {
        Claims claims = this.getClaims(token);

        Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get("roles").toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        return new UsernamePasswordAuthenticationToken(new Account(claims, authorities), token, authorities);
    }

}