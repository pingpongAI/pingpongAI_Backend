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
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
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
@Table(
    name = "member",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = "email"),
    }
)
public class Member extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "member_id")
  private Long id;

  @OneToMany(mappedBy = "member")
  private List<Train> trains = new ArrayList<>();

  @Column(nullable = false, length = 30)
  private String email;

  @Column(nullable = false)
  private String password;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String tel;

  @Enumerated(EnumType.STRING)
  private MemberStatus status;

  //==정보 수정==//
  public void updatePassword(PasswordEncoder passwordEncoder, String password) {
    this.password = passwordEncoder.encode(password);
  }

  //==패스워드 암호화==//
  public void encodePassword(PasswordEncoder passwordEncoder) {
    this.password = passwordEncoder.encode(password);
  }

}



