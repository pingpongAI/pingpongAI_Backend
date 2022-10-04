package com.itm.ai_pingpong.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.itm.ai_pingpong.domain.Member;
import com.itm.ai_pingpong.dto.MemberInfoDto;
import com.itm.ai_pingpong.dto.MemberSignUpDto;
import com.itm.ai_pingpong.global.exception.MemberException;
import com.itm.ai_pingpong.global.exception.MemberExceptionType;
import com.itm.ai_pingpong.reposistory.MemberRepository;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class MemberServiceImplTest {

  @Autowired
  EntityManager em;
  @Autowired
  MemberRepository memberRepository;
  @Autowired
  MemberService memberService;
  @Autowired
  PasswordEncoder passwordEncoder;

  String PASSWORD = "password";

  private void clear() {
    em.flush();
    em.clear();
  }

  private MemberSignUpDto makeMemberSignUpDto() {
    return new MemberSignUpDto("username@test.com", PASSWORD, "name", "010-1234-1234");
  }

  private MemberSignUpDto setMember() throws Exception {
    MemberSignUpDto memberSignUpDto = makeMemberSignUpDto();
    memberService.signUp(memberSignUpDto);
    clear();
    SecurityContext emptyContext = SecurityContextHolder.createEmptyContext();

    emptyContext.setAuthentication(new UsernamePasswordAuthenticationToken(User.builder()
        .username(memberSignUpDto.usermail())
        .password(memberSignUpDto.password())
        .roles("USER")
        .build(),
        null, null));

    SecurityContextHolder.setContext(emptyContext);
    return memberSignUpDto;
  }

//  @AfterEach
//  public void removeMember() {
//    SecurityContextHolder.createEmptyContext().setAuthentication(null);
//  }

  @Test
  public void 회원가입_성공() throws Exception {
    //given
    MemberSignUpDto memberSignUpDto = makeMemberSignUpDto();

    //when
    memberService.signUp(memberSignUpDto);
    clear();

    //then
    Member member = memberRepository.findByEmail(memberSignUpDto.usermail())
        .orElseThrow(() -> new MemberException(MemberExceptionType.NOT_FOUND_MEMBER));
    assertThat(member.getId()).isNotNull();
    assertThat(member.getEmail()).isEqualTo(memberSignUpDto.usermail());
    assertThat(member.getName()).isEqualTo(memberSignUpDto.name());
    assertThat(member.getTel()).isEqualTo(memberSignUpDto.tel());
  }

  @Test
  public void 회원가입_실패_원인_아이디중복() throws Exception {
    //given
    MemberSignUpDto memberSignUpDto = makeMemberSignUpDto();
    memberService.signUp(memberSignUpDto);
    clear();

    //when, then
    assertThat(assertThrows(MemberException.class,
        () -> memberService.signUp(memberSignUpDto)).getExceptionType()).isEqualTo(
        MemberExceptionType.ALREADY_EXIST_USERNAME);
  }

  @Test
  public void 회원가입_실패_입력하지않은_필드가있으면_오류() throws Exception {
    //given
    MemberSignUpDto memberSignUpDto1 = new MemberSignUpDto(null, passwordEncoder.encode(PASSWORD),
        "name", "010-1234-1234");
    MemberSignUpDto memberSignUpDto2 = new MemberSignUpDto("username@test.com", null, "name",
        "010-1234-1234");
    MemberSignUpDto memberSignUpDto3 = new MemberSignUpDto("username@test.com",
        passwordEncoder.encode(PASSWORD), null, "010-1234-1234");
    MemberSignUpDto memberSignUpDto4 = new MemberSignUpDto("username@test.com",
        passwordEncoder.encode(PASSWORD), "name", null);

    //when, then

    assertThrows(Exception.class, () -> memberService.signUp(memberSignUpDto1));

    assertThrows(Exception.class, () -> memberService.signUp(memberSignUpDto2));

    assertThrows(Exception.class, () -> memberService.signUp(memberSignUpDto3));

    assertThrows(Exception.class, () -> memberService.signUp(memberSignUpDto4));
  }

  @Test
  public void 회원정보조회() throws Exception {
    //given
    MemberSignUpDto memberSignUpDto = setMember();
    Member member = memberRepository.findByEmail(memberSignUpDto.usermail())
        .orElseThrow(() -> new Exception());
    clear();

    //when
    MemberInfoDto info = memberService.getInfo(member.getId());

    //then
    assertThat(info.getEmail()).isEqualTo(memberSignUpDto.usermail());
    assertThat(info.getName()).isEqualTo(memberSignUpDto.name());
    assertThat(info.getTel()).isEqualTo(memberSignUpDto.tel());
  }

  @Test
  public void 내정보조회() throws Exception {
    //given
    MemberSignUpDto memberSignUpDto = setMember();

    //when
    MemberInfoDto myInfo = memberService.getMyInfo();

    //then
    assertThat(myInfo.getEmail()).isEqualTo(memberSignUpDto.usermail());
    assertThat(myInfo.getName()).isEqualTo(memberSignUpDto.name());
    assertThat(myInfo.getTel()).isEqualTo(memberSignUpDto.tel());

  }

  @Test
  public void 회원탈퇴_실패_비밀번호가_일치하지않음() throws Exception {
    //given
    MemberSignUpDto memberSignUpDto = setMember();

    //when, then
    assertThat(assertThrows(MemberException.class,
        () -> memberService.withdraw(PASSWORD + "1")).getExceptionType()).isEqualTo(
        MemberExceptionType.WRONG_PASSWORD);

  }

}