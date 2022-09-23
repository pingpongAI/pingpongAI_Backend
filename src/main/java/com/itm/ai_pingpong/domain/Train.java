package com.itm.ai_pingpong.domain;

import static javax.persistence.FetchType.LAZY;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Entity
@Getter
@RequiredArgsConstructor
public class Train {

  @Id
  @GeneratedValue
  @Column(name = "train_id")
  private Long id;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @Enumerated(EnumType.STRING)
  private TrainType type;

  private int rightCount;
  private int wrongCount;

  private LocalDateTime startAt;
  private LocalDateTime endAt;

  public Train(User user, TrainType type, int rightCount, int wrongCount, LocalDateTime startAt,
      LocalDateTime endAt) {
    this.user = user;
    this.type = type;
    this.rightCount = rightCount;
    this.wrongCount = wrongCount;
    this.startAt = startAt;
    this.endAt = endAt;
  }

  @Override
  public String toString() {
    return "Train{" +
        "id=" + id +
        ", user=" + user +
        ", type=" + type +
        ", rightCount=" + rightCount +
        ", wrongCount=" + wrongCount +
        ", startAt=" + startAt +
        ", endAt=" + endAt +
        '}';
  }
}
