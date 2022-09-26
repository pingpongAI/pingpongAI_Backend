package com.itm.ai_pingpong.domain;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseTimeEntity {

  // Entity가 생성되어 저장될 때 시간이 자동 저장됩니다.
  @CreatedDate
  @Column(updatable = false)
  private LocalDateTime createdAt;

  // 조회한 Entity 값을 변경할 때 시간이 자동 저장됩니다
  @LastModifiedDate
  private LocalDateTime updatedAt;

}
