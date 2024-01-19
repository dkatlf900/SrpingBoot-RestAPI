package com.example.szssonjunyoung.core.token.filter;

import com.example.szssonjunyoung.core.token.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;


@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;


    private static final AntPathMatcher pathMatcher = new AntPathMatcher();


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, java.io.IOException {
        String url = request.getRequestURI();
/*
        if(uriVerify(url)) {
            String requestToken = request.getHeader("recaptcha_v3_token");
            if(!recaptchaService.verifyRecaptcha(requestToken)) {
                this.setCaptchaFilterErrorResponse(response, ErrorCode.ERROR_AE1004);
                return;
            }
        }*/
        filterChain.doFilter(request, response);
    }




    /*
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                FilterChain filterChain) throws ServletException, IOException, java.io.IOException {

        String url = request.getRequestURI();

        // access_token, Header 검증. (role:USER)
        if(pathMatcher.match("/api/v1/user/**", url)) {
            String requestAccessToken = request.getHeader("access_token");
            try {
                if(!ObjectUtils.isEmpty(requestAccessToken) && jwtTokenProvider.validateToken(requestAccessToken)) {
                    this.validLogoutToken(requestAccessToken);
                    this.authenticationToken(requestAccessToken);
                    filterChain.doFilter(request, response);
                    return;
                }
            } catch (FantooException e) {
                if(ErrorCode.ERROR_AE5102.getCode() == e.getErrorCode().getCode()) {
                    try {
                        // new access_token
                        String reissueToken = valueOperations.get(RedisKeyType.userReissueToken.getType() + requestAccessToken);
                        if(!ObjectUtils.isEmpty(reissueToken)) {
                            this.setReissueErrorResponse(response, reissueToken);
                            return;
                        }

                        // 만료된 Token 갱신 검증
                        this.validRefreshToken(url, requestAccessToken);
                        RequestDispatcher dispatcher = request.getRequestDispatcher(REISSUE_URL);
                        dispatcher.forward(request, response);
                        return;
                    } catch (FantooException ee) {
                        setFilterErrorResponse(ErrorCode.ERROR_401.getStatus(), response, ee, ee.getErrorCode());
                        return;
                    }
                }

                setFilterErrorResponse(ErrorCode.ERROR_401.getStatus(), response, e, e.getErrorCode());
                return;
            } catch (Exception e) {
                setFilterErrorResponse(ErrorCode.ERROR_401.getStatus(), response, e, null);
                return;
            }

        }


        // SSO WEB - Authorization, Header 검증. (role:ACCOUNT)
        if(pathMatcher.match("/api/v1/account/**", url)) {
            String authorizationHeader = request.getHeader("Authorization");

            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                String jwtToken = authorizationHeader.substring(7);
                if(!ObjectUtils.isEmpty(jwtToken) && jwtTokenProvider.validateToken(jwtToken)) {
                    this.authenticationToken(jwtToken);
                    filterChain.doFilter(request, response);
                    return;
                }
            }
        }

        filterChain.doFilter(request, response);
    }


    private void authenticationToken(String token) {
        Authentication authentication = jwtTokenProvider.getAuthentication(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }


    private void validLogoutToken(String requestAccessToken) {
        if(!ObjectUtils.isEmpty(valueOperations.get(RedisKeyType.userLogout.getType() + requestAccessToken))) {
            throw new FantooException(ErrorCode.ERROR_AE5103);
        }
    }


    *//**
     *  refresh_token 검증
     *//*
    private void validRefreshToken(String url, String accessToken) {
        // refreshToken 존재여부 검증.
        String getRefreshToken = valueOperations.get(RedisKeyType.userRefreshToken.getType() + accessToken);
        if(ObjectUtils.isEmpty(getRefreshToken)) {
            throw new FantooException(ErrorCode.ERROR_AE5101);
        }
        jwtTokenProvider.validateToken(getRefreshToken);
    }


    *//**
     * Filter Error 처리
     *//*
    private void setFilterErrorResponse(HttpStatus status, HttpServletResponse response, Throwable e, ErrorCode errorCode) throws java.io.IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        ApiResult<Object> apiResult = new ApiResult<>();
        if(!ObjectUtils.isEmpty(errorCode)) {
            String message = me.getLocaleMsg(errorCode.getCode(), Locale.KOREAN);
            apiResult.setErrorCode(errorCode.getCode());
            apiResult.setMsg("Unauthorized (" + message + ")");
        } else {
            apiResult.setErrorCode(String.valueOf(status.value()));
            apiResult.setMsg("Unauthorized (Access Token authentication failed)");
        }

        response.setStatus(status.value());
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(apiResult));
    }


    *//**
     * 신규 access_token response만 사용
     *//*
    private void setReissueErrorResponse(HttpServletResponse response, String newAccessToken) throws java.io.IOException {
        Map<String, Object> map = new HashMap<>();
        map.put("access_token", newAccessToken);

        ApiResult<Object> apiResult = new ApiResult<>(ErrorCode.ERROR_AE5102.getCode(), "Unauthorized (Access Token expired, Please use the new token.)");
        apiResult.setDataObj(map);

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(new ObjectMapper().writeValueAsString(apiResult));
    }*/
}