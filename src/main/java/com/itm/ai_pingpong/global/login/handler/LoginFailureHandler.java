package com.itm.ai_pingpong.global.login.handler;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

@Slf4j
public class LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

  @Override
  public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException exception) throws IOException, ServletException {
//    response.setStatus(HttpServletResponse.SC_OK);//보안을 위해 로그인 오류지만 200 반환
    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);//로그인 오류지만 400 반환

    response.getWriter().write("fail");
    log.info("로그인에 실패했습니다");
  }

}
