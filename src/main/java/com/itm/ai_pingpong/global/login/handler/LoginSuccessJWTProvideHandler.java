package com.itm.ai_pingpong.global.login.handler;

import com.itm.ai_pingpong.global.jwt.service.JwtService;
import com.itm.ai_pingpong.reposistory.MemberRepository;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional
public class LoginSuccessJWTProvideHandler extends SimpleUrlAuthenticationSuccessHandler {

  private final JwtService jwtService;
  private final MemberRepository memberRepository;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {

    String usermail = extractUsermail(authentication);
    String accessToken = jwtService.createAccessToken(usermail);
    String refreshToken = jwtService.createRefreshToken();

    jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken);
    memberRepository.findByEmail(usermail).ifPresent(
        member -> member.updateRefreshToken(refreshToken)
    );

    log.info("로그인에 성공합니다 JWT를 발급합니다. usermail: {}", usermail);
    log.info("AccessToken을 발급합니다. AccessToken: {}", accessToken);
    log.info("RefreshToken을 발급합니다. RefreshToken: {}", refreshToken);

    response.getWriter().write("success");
  }

  private String extractUsermail(Authentication authentication) {
    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    return userDetails.getUsername();
  }
}