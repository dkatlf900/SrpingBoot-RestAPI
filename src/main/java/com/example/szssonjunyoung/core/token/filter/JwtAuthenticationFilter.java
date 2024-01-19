package com.example.szssonjunyoung.core.token.filter;

import com.example.szssonjunyoung.core.token.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.Arrays;
import java.util.List;


@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;


    private static final AntPathMatcher pathMatcher = new AntPathMatcher();

    private List<String> uriExcludeTokenVerify = Arrays.asList(
            "/szs/login",
            "/szs/sign"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, java.io.IOException {
        String url = request.getRequestURI();

        if(pathMatcher.match("/szs/**", url)) {
            /**
             * 토큰 검증
             */
            // TODO 여기서 검증잘되는지 확인필요.
            if(uriVerify(url)) {
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

    private void authenticationToken(String token) {
        Authentication authentication = jwtTokenProvider.getAuthentication(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}