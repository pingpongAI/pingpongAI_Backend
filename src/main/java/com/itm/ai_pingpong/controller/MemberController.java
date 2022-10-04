package com.itm.ai_pingpong.controller;

import com.itm.ai_pingpong.dto.MemberInfoDto;
import com.itm.ai_pingpong.dto.MemberSignUpDto;
import com.itm.ai_pingpong.dto.MemberUpdateDto;
import com.itm.ai_pingpong.dto.MemberWithdrawDto;
import com.itm.ai_pingpong.dto.UpdatePasswordDto;
import com.itm.ai_pingpong.service.MemberService;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {

  private final MemberService memberService;

  /**
   * 회원가입
   */
  @PostMapping("/signUp")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<MemberSignUpDto> signUp(@Valid @RequestBody MemberSignUpDto memberSignUpDto)
      throws Exception {
    MemberSignUpDto signUpDto = memberService.signUp(memberSignUpDto);
    return ResponseEntity.ok()
        .body(signUpDto);
  }

  /**
   * 회원정보수정
   */
  @PutMapping("/member")
  @ResponseStatus(HttpStatus.OK)
  public void updateBasicInfo(@Valid @RequestBody MemberUpdateDto memberUpdateDto)
      throws Exception {
    memberService.update(memberUpdateDto);
  }

  /**
   * 비밀번호 수정
   */
  @PutMapping("/member/password")
  @ResponseStatus(HttpStatus.OK)
  public void updatePassword(@Valid @RequestBody UpdatePasswordDto updatePasswordDto)
      throws Exception {
    memberService.updatePassword(updatePasswordDto.checkPassword(),
        updatePasswordDto.toBePassword());
  }

  /**
   * 회원탈퇴
   */
  @DeleteMapping("/member")
  @ResponseStatus(HttpStatus.OK)
  public void withdraw(@Valid @RequestBody MemberWithdrawDto memberWithdrawDto) throws Exception {
    memberService.withdraw(memberWithdrawDto.checkPassword());
  }

  /**
   * 회원정보조회
   */
  @GetMapping("/member/{id}")
  public ResponseEntity getInfo(@Valid @PathVariable("id") Long id) throws Exception {
    MemberInfoDto info = memberService.getInfo(id);
    return new ResponseEntity(info, HttpStatus.OK);
  }

  /**
   * 내정보조회
   */
  @GetMapping("/member")
  public ResponseEntity getMyInfo(HttpServletResponse response) throws Exception {
    MemberInfoDto myInfo = memberService.getMyInfo();
    return new ResponseEntity(myInfo, HttpStatus.OK);
  }
}

