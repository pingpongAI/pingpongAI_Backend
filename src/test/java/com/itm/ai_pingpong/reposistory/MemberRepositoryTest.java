package com.itm.ai_pingpong.reposistory;

import static org.assertj.core.api.Assertions.assertThat;

import com.itm.ai_pingpong.domain.Member;
import com.itm.ai_pingpong.domain.MemberStatus;
import java.util.Optional;
import javax.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

  @Autowired
  MemberRepository memberRepository;

  @AfterEach
  public void clear() {
    memberRepository.deleteAllInBatch();
  }

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

}