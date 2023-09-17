package com.itm.ai_pingpong.dto;

import com.itm.ai_pingpong.domain.Member;
import com.itm.ai_pingpong.domain.MemberStatus;
import lombok.Builder;
import lombok.Data;

@Data
public class MemberInfoDto {

  private final String email;
  private final String name;
  private final String tel;
  private final MemberStatus status;

  @Builder
  public MemberInfoDto(Member member) {
    this.email = member.getEmail();
    this.name = member.getName();
    this.tel = member.getTel();
    this.status = member.getStatus();
  }
}
