package com.itm.ai_pingpong.domain;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Member extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "member_id")
  private Long id;

  @OneToMany(mappedBy = "member")
  private List<Train> trains = new ArrayList<>();

  @Column(nullable = false, length = 30, unique = true)
  private String email;

  @Column(nullable = false)
  private String password;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String tel;

  @Enumerated(EnumType.STRING)
  private MemberStatus status;

  @Column(length = 1000)
  private String refreshToken; // RefreshToken

  //==정보 수정==//
  public void updatePassword(PasswordEncoder passwordEncoder, String password) {
    this.password = passwordEncoder.encode(password);
  }

  public void updateMail(String mail) {
    this.email = mail;

  }

  public void updateName(String name) {
    this.name = name;
  }

  public void updateTel(String tel) {
    this.tel = tel;
  }

  public void updateRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
  }

  public void destroyRefreshToken() {
    this.refreshToken = null;
  }

  //==패스워드 암호화==//
  public void encodePassword(PasswordEncoder passwordEncoder) {
    this.password = passwordEncoder.encode(password);
  }

  public boolean matchPassword(PasswordEncoder passwordEncoder, String checkPassword) {

    return false;
  }
}



