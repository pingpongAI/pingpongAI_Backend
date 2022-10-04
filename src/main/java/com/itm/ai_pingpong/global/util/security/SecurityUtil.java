package com.itm.ai_pingpong.global.util.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class SecurityUtil {

  public static String getLoginUsername() {

    // TODO: 이곳에, 로그인한 유저가 있으면 반환, 없으면 예외 발생

    UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication()
        .getPrincipal();

    return user.getUsername();
  }
}
