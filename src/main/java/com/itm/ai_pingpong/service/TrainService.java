package com.itm.ai_pingpong.service;

import com.itm.ai_pingpong.domain.Member;
import com.itm.ai_pingpong.dto.TrainResponseDto;
import com.itm.ai_pingpong.reposistory.MemberRepository;
import com.itm.ai_pingpong.reposistory.TrainRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class TrainService {

  private final MemberRepository memberRepository;
  private final TrainRepository trainRepository;

  /*
    테스트
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
      Member member = memberRepository.save(getMember());
      Member member2 = memberRepository.save(getMember2());
      for (int i = 1; i <= 25; ++i) {
        trainRepository.save(Train.builder().member(member)
            .type(TrainType.FOREHAND)
            .wrongCount(i)
            .rightCount(25 - i)
            .build());
        trainRepository.save(Train.builder().member(member2)
            .type(TrainType.FOREHAND)
            .wrongCount(i)
            .rightCount(25 - i)
            .build());
      }
    }

    private Member getMember() {
      Member member = Member.builder()
          .email("kim@naver.com")
          .password("password")
          .tel("010-1234-1234")
          .name("kim").
          status(MemberStatus.BASIC)
          .build();
      member.encodePassword(passwordEncoder);
      return member;
    }


    private Member getMember2() {
      Member member = Member.builder()
          .email("kim2@naver.com")
          .password("password")
          .tel("010-1234-1234")
          .name("kim").
          status(MemberStatus.BASIC)
          .build();
      member.encodePassword(passwordEncoder);
      return member;
    }
  */
  public Page<TrainResponseDto> findAll(long id, Pageable pageable) {
    Optional<Member> findMember = memberRepository.findById(id);
    return trainRepository.findByMemberOrderByIdDesc(findMember, pageable)
        .map(TrainResponseDto::from);
  }

}
