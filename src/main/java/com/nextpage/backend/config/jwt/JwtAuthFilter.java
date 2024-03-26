package com.nextpage.backend.config.jwt;

import com.nextpage.backend.config.auth.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthFilter extends GenericFilterBean {
    private final TokenService tokenService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        //HttpServletRequest에서 헤더에 "X-AUTH-TOKEN"에 작성된 token을 가져옴
//        String token = tokenService.resolveToken((HttpServletRequest) request);
//
//        //헤더에 작성된 토큰이 있는지 확인하고, 토큰이 만료되었는지 확인
//        if (token != null && tokenService.validateToken(token)) {
//            //토큰에서 secret key를 사용하여 회원의 이메일을 가져옴
//            String email = tokenService.getEmail(token);
//
//            //인증된 회원의 정보를 SecurityContextHolder에 저장
//            Authentication auth = new UsernamePasswordAuthenticationToken(email, "");
//            SecurityContextHolder.getContext().setAuthentication(auth);
//        }
//
//        //토큰이 없거나 만료된 토큰이라면, 다시 소셜 로그인을 진행하는 과정을 수행
//        chain.doFilter(request, response);
    }
}
