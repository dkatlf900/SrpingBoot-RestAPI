package com.example.szssonjunyoung.core.token.filter;

import com.example.szssonjunyoung.base.dto.GeneralResponse;
import com.example.szssonjunyoung.base.service.MessageSourceService;
import com.example.szssonjunyoung.core.aop.exception.ErrorCode;
import com.example.szssonjunyoung.core.aop.exception.custom.SzsException;
import com.example.szssonjunyoung.core.token.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;


@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    private MessageSourceService me;


    private static final AntPathMatcher pathMatcher = new AntPathMatcher();

    private List<String> uriExcludeTokenVerify = Arrays.asList(
            "/szs/login",
            "/szs/signup"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, java.io.IOException {
        String url = request.getRequestURI();

        if(pathMatcher.match("/szs/**", url)) {
            /**
             * 토큰 검증
             */
            if(uriVerify(url)) {
                String authorizationHeader = request.getHeader("Authorization");

                if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                    String jwtToken = authorizationHeader.substring(7);
                    try {
                        if (!ObjectUtils.isEmpty(jwtToken) && jwtTokenProvider.validateToken(jwtToken)) {
                            this.authenticationToken(jwtToken);
                            filterChain.doFilter(request, response);
                            return;
                        }
                    } catch (SzsException e) {
                        this.setFilterErrorResponse(ErrorCode.ERROR_401.getStatus(), response, e, e.getErrorCode());
                        return;
                    }
                }
            }
        }
        filterChain.doFilter(request, response);
    }

    private boolean uriVerify(String url) {
        for (String pattern : uriExcludeTokenVerify) {
            if (pathMatcher.match(pattern, url)) {
                return false;
            }
        }
        return true;
    }


    /**
     * SecurityContextHolder 인증처리
     */
    private void authenticationToken(String token) {
        Authentication authentication = jwtTokenProvider.getAuthentication(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }


    /**
     * Filter Error 처리
     */
    private void setFilterErrorResponse(HttpStatus status, HttpServletResponse response, Throwable e, ErrorCode errorCode) throws java.io.IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        GeneralResponse generalResponse = new GeneralResponse();
        if(!ObjectUtils.isEmpty(errorCode)) {
            String message = me.getLocaleMsg(errorCode.getCode(), Locale.KOREAN);
            generalResponse.setCode(errorCode.getCode());
            generalResponse.setMsg("Unauthorized (" + message + ")");
        } else {
            generalResponse.setCode(String.valueOf(status.value()));
            generalResponse.setMsg("Unauthorized (Access Token authentication failed)");
        }

        response.setStatus(status.value());
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(generalResponse));
    }
}