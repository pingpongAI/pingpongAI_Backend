package com.itm.ai_pingpong.dto;

import com.itm.ai_pingpong.domain.Member;
import com.itm.ai_pingpong.domain.MemberStatus;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public record MemberSignUpDto(
    @NotBlank(message = "이메일을 입력해주세요") @Email(message = "이메일 형식에 맞춰주세요")
    String usermail,
    @NotBlank(message = "비밀번호를 입력해주세요")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,30}$",
        message = "비밀번호는 8~30 자리이면서 1개 이상의 알파벳, 숫자, 특수문자를 포함해야합니다.")
    String password,
    @NotBlank(message = "이름을 입력해주세요") @Size(min = 2, message = "사용자 이름이 너무 짧습니다.")
    String name,
    @NotNull(message = "휴대전화 번호를 입력해주세요") @Pattern(regexp = "\\d{3}-\\d{4}-\\d{4}")
    String tel) {

  public Member toEntity() {
    return Member.builder()
        .email(usermail)
        .password(password)
        .name(name)
        .tel(tel)
        .status(MemberStatus.BASIC)
        .build();
  }

}
