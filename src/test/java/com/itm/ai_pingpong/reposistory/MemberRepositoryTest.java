package com.itm.ai_pingpong.reposistory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.itm.ai_pingpong.domain.Member;
import com.itm.ai_pingpong.domain.MemberStatus;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

  @Autowired
  MemberRepository memberRepository;

  @Test
  void 회원가입성공테스트() throws Exception {
    //given
    Member member = Member.builder()
        .email("test@test.com")
        .tel("010-1212-1212")
        .status(MemberStatus.BASIC)
        .password("test")
        .name("testMember")
        .build();

    //when
    Member saveMember = memberRepository.save(member);

    //then
    Optional<Member> findMember = memberRepository.findById(saveMember.getId());
    assertThat(findMember.get().getId()).isEqualTo(saveMember.getId());
  }

  @Test
  public void 오류_회원가입시_이메일_없음() throws Exception {
    //given
    Member member = Member.builder()
//        .email("test@test.com")
        .tel("010-1212-1212")
        .status(MemberStatus.BASIC)
        .password("test")
        .name("testMember")
        .build();

    //when, then
    assertThatThrownBy(() -> memberRepository.save(member))
        .isInstanceOf(DataIntegrityViolationException.class);

  }

  @Test
  public void 오류_회원가입시_폰번호_없음() throws Exception {
    //given
    Member member = Member.builder()
        .email("test@test.com")
//        .tel("010-1212-1212")
        .status(MemberStatus.BASIC)
        .password("test")
        .name("testMember")
        .build();

    //when, then
    assertThrows(Exception.class, () -> memberRepository.save(member));
  }

  @Test
  public void 오류_회원가입시_비밀번호_없음() throws Exception {
    //given
    Member member = Member.builder()
        .email("test@test.com")
        .tel("010-1212-1212")
        .status(MemberStatus.BASIC)
//        .password("test")
        .name("testMember")
        .build();
    //when, then
    assertThrows(Exception.class, () -> memberRepository.save(member));
  }

  @Test
  public void 오류_회원가입시_이름_없음() throws Exception {
    //given
    Member member = Member.builder()
        .email("test@test.com")
        .tel("010-1212-1212")
        .status(MemberStatus.BASIC)
        .password("test")
//        .name("testMember")
        .build();

    //when, then
    assertThrows(Exception.class, () -> memberRepository.save(member));
  }

  @Test
  public void 오류_회원가입시_중복된_아이디가_있음() throws Exception {
    //given
    Member member1 = Member.builder()
        .email("test@test.com")
        .tel("010-1212-1212")
        .status(MemberStatus.BASIC)
        .password("test")
        .name("testMember")
        .build();
    memberRepository.save(member1);

    Member member2 = Member.builder()
        .email("test@test.com")
        .tel("010-1212-1212")
        .status(MemberStatus.BASIC)
        .password("test")
        .name("testMember")
        .build();

    //when, then
    assertThrows(Exception.class, () -> memberRepository.save(member2));

  }

  @Test
  public void 성공_회원수정() throws Exception {
    //given
    Member member1 = Member.builder()
        .email("test@test.com")
        .tel("010-1212-1212")
        .status(MemberStatus.BASIC)
        .password("test")
        .name("testMember")
        .build();

    memberRepository.save(member1);

    String updatePassword = "updatePassword";
    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    //when
    Member findMember = memberRepository.findById(member1.getId())
        .orElseThrow(() -> new Exception());
    findMember.updatePassword(passwordEncoder, updatePassword);

    //then
    Member findUpdateMember = memberRepository.findById(findMember.getId())
        .orElseThrow(() -> new Exception());

    assertThat(findUpdateMember).isSameAs(findMember);
    assertThat(passwordEncoder.matches(updatePassword, findUpdateMember.getPassword())).isTrue();
  }

  @Test
  public void 성공_회원삭제() throws Exception {
    //given
    Member member1 = Member.builder()
        .email("test@test.com")
        .tel("010-1212-1212")
        .status(MemberStatus.BASIC)
        .password("test")
        .name("testMember")
        .build();
    memberRepository.save(member1);

    //when
    memberRepository.delete(member1);

    //then
    assertThrows(Exception.class,
        () -> memberRepository.findById(member1.getId())
            .orElseThrow(() -> new Exception()));
  }


  @Test
  public void existByEmail_정상작동() throws Exception {
    //given
    String userEmail = "test@test.com";
    Member member1 = Member.builder()
        .email(userEmail)
        .tel("010-1212-1212")
        .status(MemberStatus.BASIC)
        .password("test")
        .name("testMember")
        .build();
    memberRepository.save(member1);

    //when, then
    assertThat(memberRepository.existsByEmail(userEmail)).isTrue();
    assertThat(memberRepository.existsByEmail(userEmail + "123")).isFalse();

  }


  @Test
  public void findByEmail_정상작동() throws Exception {
    //given
    String userEmail = "test@test.com";
    Member member1 = Member.builder()
        .email(userEmail)
        .tel("010-1212-1212")
        .status(MemberStatus.BASIC)
        .password("test")
        .name("testMember")
        .build();
    memberRepository.save(member1);

    //when, then
    assertThat(memberRepository.findByEmail(userEmail).get().getEmail()).isEqualTo(
        member1.getEmail());
    assertThat(memberRepository.findByEmail(userEmail).get().getName()).isEqualTo(
        member1.getName());
    assertThat(memberRepository.findByEmail(userEmail).get().getId()).isEqualTo(member1.getId());
    assertThrows(Exception.class,
        () -> memberRepository.findByEmail(userEmail + "123")
            .orElseThrow(() -> new Exception()));

  }

  @Test
  public void 회원가입시_생성시간_등록() throws Exception {
    //given
    Member member1 = Member.builder()
        .email("test@test.com")
        .tel("010-1212-1212")
        .status(MemberStatus.BASIC)
        .password("test")
        .name("testMember")
        .build();
    memberRepository.save(member1);

    //when
    Member findMember = memberRepository.findById(member1.getId())
        .orElseThrow(() -> new Exception());

    //then
    assertThat(findMember.getCreatedAt()).isNotNull();
    assertThat(findMember.getUpdatedAt()).isNotNull();

  }


}