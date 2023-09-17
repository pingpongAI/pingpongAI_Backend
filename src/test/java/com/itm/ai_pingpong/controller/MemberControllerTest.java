package com.itm.ai_pingpong.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itm.ai_pingpong.domain.Member;
import com.itm.ai_pingpong.dto.MemberSignUpDto;
import com.itm.ai_pingpong.global.exception.MemberExceptionType;
import com.itm.ai_pingpong.reposistory.MemberRepository;
import com.itm.ai_pingpong.service.MemberService;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class MemberControllerTest {

  private static final String BEARER = "Bearer ";
  private static String SIGN_UP_URL = "/signUp";
  @Autowired
  MockMvc mockMvc;
  @Autowired
  EntityManager em;
  @Autowired
  MemberService memberService;
  @Autowired
  MemberRepository memberRepository;
  ObjectMapper objectMapper = new ObjectMapper();
  @Autowired
  PasswordEncoder passwordEncoder;
  private String usermail = "usermail@test.com";
  private String password = "password123!";
  private String name = "name";
  private String tel = "010-1234-1234";
  @Value("${jwt.access.header}")
  private String accessHeader;

  private void clear() {
    em.flush();
    em.clear();
  }

  private void signUp(String signUpData) throws Exception {
    System.out.println("signUpData = " + signUpData);
    mockMvc.perform(
            post(SIGN_UP_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(signUpData))
        .andExpect(status().isOk());
  }

  private void signUpFail(String signUpData) throws Exception {
    System.out.println("signUpData = " + signUpData);
    mockMvc.perform(
            post(SIGN_UP_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(signUpData))
        .andExpect(status().isBadRequest());
  }

  private String getAccessToken() throws Exception {

    Map<String, String> map = new HashMap<>();
    map.put("usermail", usermail);
    map.put("password", password);

    MvcResult result = mockMvc.perform(
            post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(map)))
        .andExpect(status().isOk()).andReturn();

    return result.getResponse().getHeader(accessHeader);
  }

  @Test
  void 회원가입_성공() throws Exception {
    //given
    String signUpData = objectMapper.writeValueAsString(
        new MemberSignUpDto(usermail = usermail, password = password, name = name, tel = tel));

    //when
    signUp(signUpData);

    //then
    Member findMember = memberRepository.findByEmail(usermail)
        .orElseThrow(() -> new Exception("회원이 없습니다"));
    assertThat(findMember.getEmail()).isEqualTo(usermail);
    assertThat(memberRepository.findAll().size()).isEqualTo(1);

  }

  @Test
  void 회원가입_실패_중복아이디() throws Exception {
    //given
    String signUpData = objectMapper.writeValueAsString(
        new MemberSignUpDto(usermail = usermail, password = password, name = name, tel = tel));

    String signUpData2 = objectMapper.writeValueAsString(
        new MemberSignUpDto(usermail = usermail, password = password, name = name, tel = tel));

    //when
    signUp(signUpData);

    //then
    assertThrows(AssertionError.class, () -> signUp(signUpData2));
  }

  @Test
  public void 회원가입_실패_필드가_없음() throws Exception {
    //given
    String noUsernameSignUpData = objectMapper.writeValueAsString(
        new MemberSignUpDto(usermail = null, password = password, name = name, tel = tel));
    String noPasswordSignUpData = objectMapper.writeValueAsString(
        new MemberSignUpDto(usermail = usermail, password = null, name = name, tel = tel));
    String noNameSignUpData = objectMapper.writeValueAsString(
        new MemberSignUpDto(usermail = usermail, password = password, name = null, tel = tel));
    String noTelSignUpData = objectMapper.writeValueAsString(
        new MemberSignUpDto(usermail = usermail, password = password, name = name, tel = null));

    signUpFail(noUsernameSignUpData);//예외가 발생하면 상태코드는 400
    signUpFail(noPasswordSignUpData);//예외가 발생하면 상태코드는 400
    signUpFail(noNameSignUpData);//예외가 발생하면 상태코드는 400
    signUpFail(noTelSignUpData);//예외가 발생하면 상태코드는 400

    assertThat(memberRepository.findAll().size()).isEqualTo(0);
  }

  @Test
  public void 내정보조회_성공() throws Exception {
    //given
    String signUpData = objectMapper.writeValueAsString(
        new MemberSignUpDto(usermail, password, name, tel));
    signUp(signUpData);

    String accessToken = getAccessToken();

    //when
    MvcResult result = mockMvc.perform(
            get("/member")
                .characterEncoding(StandardCharsets.UTF_8)
                .header(accessHeader, BEARER + accessToken))
        .andExpect(status().isOk()).andReturn();

    //then
    Map<String, String> map = objectMapper.readValue(result.getResponse().getContentAsString(),
        Map.class);
    Member member = memberRepository.findByEmail(usermail)
        .orElseThrow(() -> new Exception("회원이 없습니다"));
    assertThat(member.getTel()).isEqualTo(map.get("tel"));
    assertThat(member.getEmail()).isEqualTo(map.get("email"));
    assertThat(member.getName()).isEqualTo(map.get("name"));
  }

  @Test
  public void 내정보조회_실패_JWT없음() throws Exception {
    //given
    String signUpData = objectMapper.writeValueAsString(
        new MemberSignUpDto(usermail, password, name, tel));
    System.out.println("signUpData = " + signUpData);
    signUp(signUpData);

    String accessToken = "getAccessToken()";
    System.out.println("accessToken = " + accessToken);

    //when,then
    mockMvc.perform(
            get("/member")
                .characterEncoding(StandardCharsets.UTF_8)
                .header(accessHeader, BEARER + accessToken + 1))
        .andExpect(status().isForbidden());

  }

  @Test
  public void 회원정보조회_실패_없는회원조회() throws Exception {
    //given
    String signUpData = objectMapper.writeValueAsString(
        new MemberSignUpDto(usermail, password, name, tel));
    signUp(signUpData);

    String accessToken = getAccessToken();

    //when
    MvcResult result = mockMvc.perform(
            get("/member/11111")
                .characterEncoding(StandardCharsets.UTF_8)
                .header(accessHeader, BEARER + accessToken))
        .andExpect(status().isNotFound()).andReturn();

    //then
    Map<String, Integer> map = objectMapper.readValue(result.getResponse().getContentAsString(),
        Map.class);
    assertThat(map.get("errorCode")).isEqualTo(
        MemberExceptionType.NOT_FOUND_MEMBER.getErrorCode());//빈 문자열
  }

  @Test
  public void 비밀번호수정_실패_검증비밀번호가_틀림() throws Exception {
    //given
    String signUpData = objectMapper.writeValueAsString(
        new MemberSignUpDto(usermail, password, name, tel));
    signUp(signUpData);

    String accessToken = getAccessToken();

    Map<String, Object> map = new HashMap<>();
    map.put("checkPassword", password + "1");
    map.put("toBePassword", password + "!@#@!#@!#");

    String updatePassword = objectMapper.writeValueAsString(map);

    //when
    mockMvc.perform(
            put("/member/password")
                .header(accessHeader, BEARER + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatePassword))
        .andExpect(status().isBadRequest());

    //then
    Member member = memberRepository.findByEmail(usermail)
        .orElseThrow(() -> new Exception("회원이 없습니다"));
    assertThat(passwordEncoder.matches(password, member.getPassword())).isTrue();
    assertThat(passwordEncoder.matches(password + "!@#@!#@!#", member.getPassword())).isFalse();
  }

  @Test
  public void 회원탈퇴_실패_비밀번호틀림() throws Exception {
    //given
    String signUpData = objectMapper.writeValueAsString(
        new MemberSignUpDto(usermail, password, name, tel));
    signUp(signUpData);

    String accessToken = getAccessToken();

    Map<String, Object> map = new HashMap<>();
    map.put("checkPassword", password + 11);

    String updatePassword = objectMapper.writeValueAsString(map);

    //when
    mockMvc.perform(
            delete("/member")
                .header(accessHeader, BEARER + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatePassword))
        .andExpect(status().isBadRequest());

    //then
    Member member = memberRepository.findByEmail(usermail)
        .orElseThrow(() -> new Exception("회원이 없습니다"));
    assertThat(member).isNotNull();
  }

  @Test
  public void 비밀번호수정_실패_바꾸려는_비밀번호_형식_올바르지않음() throws Exception {
    //given
    String signUpData = objectMapper.writeValueAsString(
        new MemberSignUpDto(usermail, password, name, tel));
    signUp(signUpData);

    String accessToken = getAccessToken();

    Map<String, Object> map = new HashMap<>();
    map.put("checkPassword", password);
    map.put("toBePassword", "123123");

    String updatePassword = objectMapper.writeValueAsString(map);

    //when
    mockMvc.perform(
            put("/member/password")
                .header(accessHeader, BEARER + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatePassword))
        .andExpect(status().isBadRequest());

    //then
    Member member = memberRepository.findByEmail(usermail)
        .orElseThrow(() -> new Exception("회원이 없습니다"));
    assertThat(passwordEncoder.matches(password, member.getPassword())).isTrue();
    assertThat(passwordEncoder.matches("123123", member.getPassword())).isFalse();
  }

}