package com.itm.ai_pingpong.reposistory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.itm.ai_pingpong.domain.User;
import com.itm.ai_pingpong.domain.UserStatus;
import javax.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
@Transactional
class UserRepositoryTest {

  @Autowired
  UserRepository userRepository;

  @Test
  void 이메일중복테스트() throws Exception {
    //given
    User user1 = User.builder()
        .email("qw@qw.com")
        .tel("010-1212-1212")
        .status(UserStatus.BASIC)
        .password("asd")
        .name("user")
        .build();
    userRepository.save(user1);

    User user2 = User.builder()
        .email("qw@qw.com")
        .tel("0110-1212-1212")
        .status(UserStatus.BASIC)
        .password("1asd")
        .name("user")
        .build();

    //when  then
    assertThrows(DataIntegrityViolationException.class, () -> userRepository.saveAndFlush(user2));
    userRepository.delete(user1);

  }

  @Test
  void 회원가입테스트() throws Exception {
    //given
    User user = User.builder()
        .email("q1w@qw.com")
        .tel("010-1212-1212")
        .status(UserStatus.BASIC)
        .password("asd")
        .name("user")
        .build();

    //when
    User saveUser = userRepository.save(user);

    //then
    User findUser = userRepository.findById(saveUser.getId())
        .orElseThrow(() -> new RuntimeException("저장된 회원이 없습니다"));

    assertThat(findUser).isEqualTo(saveUser);
    assertThat(findUser).isEqualTo(user);
  }

  @Test
  void 회원가입시패스워드없음오류() throws Exception {
    //given
    User user = User.builder()
        .email("test@test.com")
//        .password("test")
        .name("test")
        .status(UserStatus.BASIC)
        .tel("010-1234-1234")
        .build();

    //when //then
    assertThrows(Exception.class, () -> userRepository.save(user));

  }

  @Test
  void 회원가입시이름없음오류() throws Exception {
    //given
    User user = User.builder()
        .email("test@test.com")
        .password("test")
//        .name("test")
        .status(UserStatus.BASIC)
        .tel("010-1234-1234")
        .build();

    //when //then
    assertThrows(Exception.class, () -> userRepository.save(user));

  }


  @Test
  void 회원가입시번호없음오류() throws Exception {
    //given
    User user = User.builder()
        .email("test@test.com")
        .password("test")
        .name("test")
        .status(UserStatus.BASIC)
//        .tel("010-1234-1234")
        .build();

    //when //then
    assertThrows(Exception.class, () -> userRepository.save(user));

  }

  @Test
  void 회원비밀번호수정() throws Exception {
    //given
    String prePassword = "test1";

    User user = User.builder()
        .email("te12st@test.com")
        .password(prePassword)
        .name("test")
        .status(UserStatus.BASIC)
        .tel("010-1234-1234")
        .build();

    userRepository.save(user);

    String newPassword = "test2";
    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    //when
    User findUser = userRepository.findById(user.getId()).orElseThrow(() -> new Exception());
    findUser.updatePassword(passwordEncoder, newPassword);

    //then
    User findUpdateUser = userRepository.findById(user.getId()).orElseThrow(() -> new Exception());

    assertThat(passwordEncoder.matches(newPassword, findUpdateUser.getPassword())).isTrue();
  }

  @Test
  public void 회원삭제() throws Exception {
    //given
    User user = User.builder()
        .email("test123@test.com")
        .password("test")
        .name("test1")
        .status(UserStatus.BASIC)
        .tel("010-1234-1234")
        .build();
    userRepository.save(user);

    //when
    userRepository.delete(user);

    //then
    assertThrows(Exception.class,
        () -> userRepository.findById(user.getId()).orElseThrow(() -> new Exception()));
  }

  @Test
  public void existByUserEmail() throws Exception {
    //given
    String userEmail = "test1@test.com";
    User user = User.builder()
        .email(userEmail)
        .password("test")
        .name("test")
        .status(UserStatus.BASIC)
        .tel("010-1234-1234")
        .build();
    userRepository.save(user);

    //when, then
    assertThat(userRepository.existsByEmail(userEmail)).isTrue();
    assertThat(userRepository.existsByEmail(userEmail + "123")).isFalse();

  }

  @Test
  @Transactional
  void findByUserEmail() throws Exception {
    //given
    String userEmail = "test@test.com";
    User user = User.builder()
        .email(userEmail)
        .password("test")
        .name("test")
        .status(UserStatus.BASIC)
        .tel("010-1234-1234")
        .build();
    userRepository.saveAndFlush(user);

    //when then
    assertThat(userRepository.findByEmail(userEmail).get().getEmail()).isEqualTo(userEmail);
    assertThat(userRepository.findByEmail(userEmail).get().getName()).isEqualTo("test");
    assertThat(userRepository.findByEmail(userEmail).get().getTel()).isEqualTo("010-1234-1234");

  }

  @Test
  public void 회원가입시_생성시간_등록() throws Exception {
    //given
    String userEmail = "test12@test.com";
    User user = User.builder()
        .email(userEmail)
        .password("test")
        .name("test")
        .status(UserStatus.BASIC)
        .tel("010-1234-1234")
        .build();
    userRepository.save(user);

    //when
    User findUser = userRepository.findById(user.getId()).orElseThrow(() -> new Exception());

    //then
    assertThat(findUser.getCreatedAt()).isNotNull();
    assertThat(findUser.getUpdatedAt()).isNotNull();

  }
}