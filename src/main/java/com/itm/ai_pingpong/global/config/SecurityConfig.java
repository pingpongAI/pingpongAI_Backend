package com.itm.ai_pingpong.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itm.ai_pingpong.global.jwt.filter.JwtAuthenticationProcessingFilter;
import com.itm.ai_pingpong.global.jwt.service.JwtService;
import com.itm.ai_pingpong.global.login.filter.JsonUsernamePasswordAuthenticationFilter;
import com.itm.ai_pingpong.global.login.handler.LoginFailureHandler;
import com.itm.ai_pingpong.global.login.handler.LoginSuccessJWTProvideHandler;
import com.itm.ai_pingpong.reposistory.MemberRepository;
import com.itm.ai_pingpong.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  private final LoginService loginService;
  private final ObjectMapper objectMapper;
  private final JwtService jwtService;
  private final MemberRepository memberRepository;


  @Override
  protected void configure(HttpSecurity http) throws Exception {

    http
        .formLogin().disable()//formLogin 인증방법 비활성화
        .httpBasic().disable()//httpBasic 인증방법 비활성화(특정 리소스에 접근할 때 username과 password 물어봄)
        .csrf().disable()
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

        .and()
        .authorizeRequests()
        .antMatchers("/login", "/signUp", "/").permitAll()
        .anyRequest().authenticated();

    http.addFilterAfter(jsonUsernamePasswordLoginFilter(), LogoutFilter.class);
    http.addFilterBefore(jwtAuthenticationProcessingFilter(),
        JsonUsernamePasswordAuthenticationFilter.class);
    // 순서가 중요하다

  }

  @Bean
  public PasswordEncoder passwordEncoder() {//1 - PasswordEncoder 등록
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager() {//2 - AuthenticationManager 등록
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();//DaoAuthenticationProvider 사용
    provider.setPasswordEncoder(
        passwordEncoder());//PasswordEncoder로는 PasswordEncoderFactories.createDelegatingPasswordEncoder() 사용
    provider.setUserDetailsService(loginService);
    return new ProviderManager(provider);
  }

  @Bean
  public LoginSuccessJWTProvideHandler loginSuccessJWTProvideHandler() {
    return new LoginSuccessJWTProvideHandler(jwtService, memberRepository);
  }

  @Bean
  public LoginFailureHandler loginFailureHandler() {
    return new LoginFailureHandler();
  }

  @Bean
  public JsonUsernamePasswordAuthenticationFilter jsonUsernamePasswordLoginFilter() {
    JsonUsernamePasswordAuthenticationFilter jsonUsernamePasswordLoginFilter = new JsonUsernamePasswordAuthenticationFilter(
        objectMapper);
    jsonUsernamePasswordLoginFilter.setAuthenticationManager(authenticationManager());
    jsonUsernamePasswordLoginFilter.setAuthenticationSuccessHandler(
        loginSuccessJWTProvideHandler());
    jsonUsernamePasswordLoginFilter.setAuthenticationFailureHandler(loginFailureHandler());
    return jsonUsernamePasswordLoginFilter;
  }

  @Bean
  public JwtAuthenticationProcessingFilter jwtAuthenticationProcessingFilter() {
    JwtAuthenticationProcessingFilter jsonUsernamePasswordLoginFilter = new JwtAuthenticationProcessingFilter(
        jwtService, memberRepository);

    return jsonUsernamePasswordLoginFilter;
  }
}