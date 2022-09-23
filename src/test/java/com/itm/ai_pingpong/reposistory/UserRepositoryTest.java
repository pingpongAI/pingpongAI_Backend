package com.itm.ai_pingpong.reposistory;

import com.itm.ai_pingpong.domain.User;
import com.itm.ai_pingpong.domain.UserStatus;
import java.time.LocalDateTime;
import javax.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserRepositoryTest {

  @Autowired
  UserRepository userRepository;

  @Test
  @Transactional
  public void testUser() {
    User user = new User("test@test.com", "test", "test", "010-1234-1234", LocalDateTime.now(),
        LocalDateTime.now(), UserStatus.BASIC);

    userRepository.save(user);
    User findUser = userRepository.findOne(1L);

    Assertions.assertThat(findUser.getId()).isEqualTo(user.getId());
  }
}