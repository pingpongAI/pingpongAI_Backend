package com.itm.ai_pingpong.reposistory;

import com.itm.ai_pingpong.domain.Train;
import com.itm.ai_pingpong.domain.TrainType;
import com.itm.ai_pingpong.domain.User;
import com.itm.ai_pingpong.domain.UserStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import javax.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

@SpringBootTest
@Transactional
class TrainRepositoryTest {

  @Autowired
  UserRepository userRepository;
  @Autowired
  TrainRepository trainRepository;

  @Test
  @Rollback(value = false)
  void save() {
    User user = new User(
        "test@test.com",
        "test",
        "test",
        "010-1234-1234",
        LocalDateTime.now(),
        LocalDateTime.now(),
        UserStatus.BASIC
    );

    userRepository.save(user);

    Train train1 = new Train(user, TrainType.FOREHAND, 10, 0, LocalDateTime.now().minusMinutes(30),
        LocalDateTime.now().minusMinutes(20));
    Train train2 = new Train(user, TrainType.BACKHAND, 10, 5, LocalDateTime.now(),
        LocalDateTime.now().plusMinutes(10));

    trainRepository.save(train1);
    trainRepository.save(train2);
  }

  @Test
  void findTrainSpecificDate() {
    LocalDate localDate = LocalDate.now();
    User findUser = userRepository.findOne(10L);

    List<Train> trainSpecificDate = trainRepository.findTrainSpecificDate(findUser, localDate);

    Assertions.assertThat(trainSpecificDate.size()).isEqualTo(2);

  }
}