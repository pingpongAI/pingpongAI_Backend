package com.itm.ai_pingpong.service;

import com.itm.ai_pingpong.domain.Member;
import com.itm.ai_pingpong.dto.MemberInfoDto;
import com.itm.ai_pingpong.dto.MemberSignUpDto;
import com.itm.ai_pingpong.dto.MemberUpdateDto;
import com.itm.ai_pingpong.global.exception.MemberException;
import com.itm.ai_pingpong.global.exception.MemberExceptionType;
import com.itm.ai_pingpong.global.util.security.SecurityUtil;
import com.itm.ai_pingpong.reposistory.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberServiceImpl implements MemberService {

  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public MemberSignUpDto signUp(MemberSignUpDto memberSignUpDto) throws MemberException {
    Member member = memberSignUpDto.toEntity();
    member.encodePassword(passwordEncoder);
    if (memberRepository.findByEmail(memberSignUpDto.usermail()).isPresent()) {
      throw new MemberException(MemberExceptionType.ALREADY_EXIST_USERNAME);
    }

    memberRepository.save(member);
    return memberSignUpDto;

  }

  @Override
  public void update(MemberUpdateDto memberUpdateDto) throws MemberException {
    Member member = memberRepository.findByEmail(SecurityUtil.getLoginUsername())
        .orElseThrow(() -> new MemberException(MemberExceptionType.NOT_FOUND_MEMBER));

    memberUpdateDto.mail().ifPresent(member::updateMail);
    memberUpdateDto.name().ifPresent(member::updateName);
    memberUpdateDto.tel().ifPresent(member::updateTel);
  }

  @Override
  public void updatePassword(String checkPassword, String toBePassword) throws MemberException {
    Member member = memberRepository.findByEmail(SecurityUtil.getLoginUsername())
        .orElseThrow(() -> new MemberException(MemberExceptionType.NOT_FOUND_MEMBER));

    if (member.matchPassword(passwordEncoder, checkPassword)) {
      throw new MemberException(MemberExceptionType.WRONG_PASSWORD);
    }

    member.updatePassword(passwordEncoder, toBePassword);
  }

  @Override
  public void withdraw(String checkPassword) throws MemberException {
    Member member = memberRepository.findByEmail(SecurityUtil.getLoginUsername())
        .orElseThrow(() -> new MemberException(MemberExceptionType.WRONG_PASSWORD));

    if (member.matchPassword(passwordEncoder, checkPassword)) {
      throw new MemberException(MemberExceptionType.WRONG_PASSWORD);
    }
    memberRepository.delete(member);

  }

  @Override
  public MemberInfoDto getInfo(Long id) throws MemberException {
    Member findMember = memberRepository.findById(id)
        .orElseThrow(() -> new MemberException(MemberExceptionType.NOT_FOUND_MEMBER));
    return new MemberInfoDto(findMember);
  }

  @Override
  public MemberInfoDto getMyInfo() throws MemberException {
    Member findMember = memberRepository.findByEmail(SecurityUtil.getLoginUsername())
        .orElseThrow(() -> new MemberException(MemberExceptionType.NOT_FOUND_MEMBER));
    return new MemberInfoDto(findMember);
  }
}
