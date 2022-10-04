package com.itm.ai_pingpong.global.login;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itm.ai_pingpong.domain.Member;
import com.itm.ai_pingpong.domain.MemberStatus;
import com.itm.ai_pingpong.reposistory.MemberRepository;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
public class LoginTest {

  private static String KEY_USERMAIL = "usermail";
  private static String KEY_PASSWORD = "password";
  private static String USERMAIL = "username@test.com";
  private static String PASSWORD = "123456789";
  private static String LOGIN_URL = "/login";

  @Value("${jwt.access.header}")
  private String accessHeader;
  @Value("${jwt.refresh.header}")
  private String refreshHeader;

  @Autowired
  MockMvc mockMvc;
  @Autowired
  MemberRepository memberRepository;
  @Autowired
  EntityManager em;
  PasswordEncoder delegatingPasswordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
  ObjectMapper objectMapper = new ObjectMapper();

  private void clear() {
    em.flush();
    em.clear();
  }


  @BeforeEach
  private void init() {
    Member member1 = Member.builder()
        .email(USERMAIL)
        .password(delegatingPasswordEncoder.encode(PASSWORD))
        .name("Member1")
        .tel("010-1234-1234")
        .status(MemberStatus.BASIC)
        .build();
    memberRepository.save(member1);
    clear();
  }

  private Map getUsernamePasswordMap(String username, String password) {
    Map<String, String> map = new HashMap<>();
    map.put(KEY_USERMAIL, username);
    map.put(KEY_PASSWORD, password);
    return map;
  }


  private ResultActions perform(String url, MediaType mediaType, Map usernamePasswordMap)
      throws Exception {
    return mockMvc.perform(MockMvcRequestBuilders
        .post(url)
        .contentType(mediaType)
        .content(objectMapper.writeValueAsString(usernamePasswordMap)));

  }

  @Test
  public void 로그인_성공() throws Exception {
    //given
    Map<String, String> map = getUsernamePasswordMap(USERMAIL, PASSWORD);

    //when, then
    MvcResult result = perform(LOGIN_URL, APPLICATION_JSON, map)
        .andDo(print())
        .andExpect(status().isOk())
        .andReturn();

  }

  @Test
  void 로그인_실패_이메일틀림() throws Exception {
    //given
    Map<String, String> map = getUsernamePasswordMap(USERMAIL + "wrong", PASSWORD);

    //when
    MvcResult result = mockMvc.perform(MockMvcRequestBuilders
            .post(LOGIN_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(map)))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andReturn();

    //then
    assertThat(result.getResponse().getHeader(accessHeader)).isNull();
    assertThat(result.getResponse().getHeader(refreshHeader)).isNull();

  }

  @Test
  void 로그인_실패_비밀번호틀림() throws Exception {
    //given
    Map<String, String> map = getUsernamePasswordMap(USERMAIL, PASSWORD + "wrong");

    //when
    MvcResult result = mockMvc.perform(MockMvcRequestBuilders
            .post(LOGIN_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(map)))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andReturn();

    //then
    assertThat(result.getResponse().getHeader(accessHeader)).isNull();
    assertThat(result.getResponse().getHeader(refreshHeader)).isNull();

  }

  @Test
  public void 로그인_주소가_틀리면_FORBIDDEN() throws Exception {
    //given
    Map<String, String> map = getUsernamePasswordMap(USERMAIL, PASSWORD);

    //when, then
    perform(LOGIN_URL + "wrong", APPLICATION_JSON, map)
        .andDo(print())
        .andExpect(status().isForbidden());

  }


  @Test
  public void 로그인_데이터형식_JSON이_아니면_400() throws Exception {
    //given
    Map<String, String> map = getUsernamePasswordMap(USERMAIL, PASSWORD);

    //when, then
    perform(LOGIN_URL, APPLICATION_FORM_URLENCODED, map)
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andReturn();
  }

  @Test
  public void 로그인_HTTP_METHOD_GET이면_NOTFOUND() throws Exception {
    //given
    Map<String, String> map = getUsernamePasswordMap(USERMAIL, PASSWORD);

    //when
    mockMvc.perform(MockMvcRequestBuilders
            .get(LOGIN_URL)
            .contentType(APPLICATION_FORM_URLENCODED)
            .content(objectMapper.writeValueAsString(map)))
        .andDo(print())
        .andExpect(status().isNotFound());
  }


  @Test
  public void 오류_로그인_HTTP_METHOD_PUT이면_NOTFOUND() throws Exception {
    //given
    Map<String, String> map = getUsernamePasswordMap(USERMAIL, PASSWORD);

    //when
    mockMvc.perform(MockMvcRequestBuilders
            .put(LOGIN_URL)
            .contentType(APPLICATION_FORM_URLENCODED)
            .content(objectMapper.writeValueAsString(map)))
        .andDo(print())
        .andExpect(status().isNotFound());
  }
}
