package com.itm.ai_pingpong.service;

import com.itm.ai_pingpong.dto.MemberInfoDto;
import com.itm.ai_pingpong.dto.MemberSignUpDto;
import com.itm.ai_pingpong.dto.MemberUpdateDto;

public interface MemberService {

  /**
   * 회원가입 정보수정 회원탈퇴 정보조회
   */

  MemberSignUpDto signUp(MemberSignUpDto memberSignUpDto) throws Exception;

  void update(MemberUpdateDto memberUpdateDto) throws Exception;

  void updatePassword(String checkPassword, String toBePassword) throws Exception;

  void withdraw(String checkPassword) throws Exception;

  MemberInfoDto getInfo(Long id) throws Exception;

  MemberInfoDto getMyInfo() throws Exception;
}
