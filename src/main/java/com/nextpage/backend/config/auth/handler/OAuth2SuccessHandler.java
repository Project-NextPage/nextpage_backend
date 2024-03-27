package com.nextpage.backend.config.auth.handler;

import com.nextpage.backend.config.jwt.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final TokenService tokenService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response
            , Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        String token = tokenService.generateToken(name, email);

//        String targetUrl = UriComponentsBuilder.fromUriString("/login")
//                .queryParam("token", token)
//                .build().toUriString();


        String targetUrl = UriComponentsBuilder.fromUriString("http://localhost:3000/oauth2/redirect")
        .queryParam("token", token)
        .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
