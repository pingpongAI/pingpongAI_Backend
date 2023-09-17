package com.itm.ai_pingpong.reposistory;

import com.itm.ai_pingpong.domain.Member;
import com.itm.ai_pingpong.domain.MemberStatus;
import com.itm.ai_pingpong.domain.Train;
import com.itm.ai_pingpong.domain.TrainType;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Transactional
class TrainRepositoryTest {

  @Autowired
  MemberRepository memberRepository;
  @Autowired
  TrainRepository trainRepository;

  Member generateMember() {
    return Member.builder()
        .email("test@test.com")
        .tel("010-1212-1212")
        .status(MemberStatus.BASIC)
        .password("test")
        .name("testMember")
        .build();
  }

  @Test
  void 훈련_저장() throws Exception {
    //given
    Member member = generateMember();
    memberRepository.save(member);

    //when
    Train train1 = Train.builder()
        .member(member)
        .type(TrainType.FOREHAND)
        .rightCount(10)
        .wrongCount(20)
        .build();
    trainRepository.save(train1);

    //then
    Optional<Train> findTrain = trainRepository.findById(train1.getId());
    Assertions.assertThat(findTrain.get()).isEqualTo(train1);

  }

  @Test
  void 모든_훈련_검색() throws Exception {
    //given
    Member member = Member.builder()
        .email("test@test.com")
        .tel("010-1212-1212")
        .status(MemberStatus.BASIC)
        .password("test")
        .name("testMember")
        .build();

    memberRepository.save(member);

    Optional<Member> findMember = memberRepository.findByEmail(member.getEmail());

    Train train1 = Train.builder()
        .member(findMember.get())
        .type(TrainType.FOREHAND)
        .rightCount(10)
        .wrongCount(20)
        .build();
    trainRepository.save(train1);

    Train train2 = Train.builder()
        .member(findMember.get())
        .type(TrainType.FOREHAND)
        .rightCount(11)
        .wrongCount(22)
        .build();
    trainRepository.save(train2);

    //when
    List<Train> trainsByUser = trainRepository.findTrainsByMember(findMember.get());

    //then
    Assertions.assertThat(trainsByUser.size()).isEqualTo(2);

  }

}


