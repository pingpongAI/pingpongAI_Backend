package com.itm.ai_pingpong.global.jwt.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.itm.ai_pingpong.reposistory.MemberRepository;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
@Setter(value = AccessLevel.PRIVATE)
@Slf4j
public class JwtServiceImpl implements JwtService {

  private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
  private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
  private static final String USERNAME_CLAIM = "usermail";
  private static final String BEARER = "Bearer ";
  private final MemberRepository memberRepository;
  @Value("${jwt.secret}")
  private String secret;
  @Value("${jwt.access.expiration}")
  private long accessTokenValidityInSeconds;
  @Value("${jwt.refresh.expiration}")
  private long refreshTokenValidityInSeconds;
  @Value("${jwt.access.header}")
  private String accessHeader;
  @Value("${jwt.refresh.header}")
  private String refreshHeader;

  @Override
  public String createAccessToken(String usermail) {
    return JWT.create()
        .withSubject(ACCESS_TOKEN_SUBJECT)
        .withExpiresAt(new Date(System.currentTimeMillis() + accessTokenValidityInSeconds * 1000))
        .withClaim(USERNAME_CLAIM, usermail)
        .sign(Algorithm.HMAC512(secret));
  }

  @Override
  public String createRefreshToken() {
    return JWT.create()
        .withSubject(REFRESH_TOKEN_SUBJECT)
        .withExpiresAt(new Date(System.currentTimeMillis() + refreshTokenValidityInSeconds + 1000))
        .sign(Algorithm.HMAC512(secret));
  }

  @Override
  public void updateRefreshToken(String usermail, String refreshToken) {
    memberRepository.findByEmail(usermail)
        .ifPresentOrElse(
            member -> member.updateRefreshToken(refreshToken),
            () -> new Exception("회원이 없습니다")
        );
  }

  @Override
  public void destroyRefreshToken(String usermail) {
    memberRepository.findByEmail(usermail)
        .ifPresentOrElse(
            member -> member.destroyRefreshToken(),
            () -> new Exception("회원이 없습니다")
        );
  }

  @Override
  public void sendAccessAndRefreshToken(HttpServletResponse response, String accessToken,
      String refreshToken) {
    response.setStatus(HttpServletResponse.SC_OK);

    setAccessTokenHeader(response, accessToken);
    setRefreshTokenHeader(response, refreshToken);

    Map<String, String> tokenMap = new HashMap<>();
    tokenMap.put(ACCESS_TOKEN_SUBJECT, accessToken);
    tokenMap.put(REFRESH_TOKEN_SUBJECT, refreshToken);
  }

  @Override
  public void sendAccessToken(HttpServletResponse response, String accessToken) {
    response.setStatus(HttpServletResponse.SC_OK);

    setAccessTokenHeader(response, accessToken);

    Map<String, String> tokenMap = new HashMap<>();
    tokenMap.put(ACCESS_TOKEN_SUBJECT, accessToken);
  }

  @Override
  public Optional<String> extractAccessToken(HttpServletRequest request) {
    return Optional.ofNullable(request.getHeader(accessHeader))
        .map(accessToken -> accessToken.replace(BEARER, ""));

  }

  @Override
  public Optional<String> extractRefreshToken(HttpServletRequest request) {
    return Optional.ofNullable(request.getHeader(refreshHeader))
        .map(refreshToken -> refreshToken.replace(BEARER, ""));


  }

  @Override
  public Optional<String> extractUsermail(String accessToken) {
    return Optional.ofNullable(
        JWT.require(Algorithm.HMAC512(secret)).build().verify(accessToken).getClaim(USERNAME_CLAIM)
            .asString());

  }

  @Override
  public void setAccessTokenHeader(HttpServletResponse response, String accessToken) {
    response.setHeader(accessHeader, accessToken);

  }

  @Override
  public void setRefreshTokenHeader(HttpServletResponse response, String refreshToken) {
    response.setHeader(refreshHeader, refreshToken);

  }

  @Override
  public boolean isTokenValid(String token) {
    try {
      JWT.require(Algorithm.HMAC512(secret)).build().verify(token);
      return true;
    } catch (Exception e) {
      log.error("유효하지 않은 Token입니다.", e.getMessage());
      return false;
    }
  }
}
