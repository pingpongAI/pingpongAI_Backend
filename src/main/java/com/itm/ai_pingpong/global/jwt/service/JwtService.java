package com.itm.ai_pingpong.global.jwt.service;

import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface JwtService {

  String createAccessToken(String usermail);

  String createRefreshToken();

  void updateRefreshToken(String usermail, String refreshToken);

  void destroyRefreshToken(String usermail);

  void sendAccessAndRefreshToken(HttpServletResponse response, String accessToken,
      String refreshToken);

  void sendAccessToken(HttpServletResponse response, String accessToken);


  Optional<String> extractAccessToken(HttpServletRequest request);

  Optional<String> extractRefreshToken(HttpServletRequest request);

  Optional<String> extractUsermail(String accessToken);


  void setAccessTokenHeader(HttpServletResponse response, String accessToken);

  void setRefreshTokenHeader(HttpServletResponse response, String refreshToken);

  boolean isTokenValid(String token);

}

