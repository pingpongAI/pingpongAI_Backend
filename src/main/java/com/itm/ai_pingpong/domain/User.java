package com.itm.ai_pingpong.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Entity
@Getter
@RequiredArgsConstructor
public class User {

  @Id
  @GeneratedValue
  @Column(name = "user_id")
  private Long id;

  @OneToMany(mappedBy = "user")
  private List<Train> trains = new ArrayList<>();

  private String email;
  private String password;
  private String name;
  private String tel;

  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  @Enumerated(EnumType.STRING)
  private UserStatus status;

  public User(String email, String password, String name, String tel, LocalDateTime createdAt,
      LocalDateTime updatedAt, UserStatus status) {
    this.email = email;
    this.password = password;
    this.name = name;
    this.tel = tel;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.status = status;
  }

}



