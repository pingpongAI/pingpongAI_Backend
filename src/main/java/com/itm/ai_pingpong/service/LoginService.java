package com.itm.ai_pingpong.service;

import com.itm.ai_pingpong.domain.Member;
import com.itm.ai_pingpong.reposistory.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService implements UserDetailsService {

  private final MemberRepository memberRepository;

  @Override
  public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
    Member member = memberRepository.findByEmail(userEmail)
        .orElseThrow(() -> new UsernameNotFoundException("이메일이 없습니다"));

    return User.builder().username(member.getEmail())
        .password(member.getPassword())
        .roles(member.getStatus().name())
        .build();
  }

}


