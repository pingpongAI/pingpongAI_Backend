package com.itm.ai_pingpong.dto;

import com.itm.ai_pingpong.domain.Train;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TrainResponseDto {

  private final Long id;
  private final int wrongCount;
  private final int rightCount;
  private final String type;
  private final LocalDate createdAt;

  public static TrainResponseDto from(Train train) {
    return TrainResponseDto.builder()
        .id(train.getId())
        .wrongCount(train.getWrongCount())
        .rightCount(train.getRightCount())
        .type(train.getType().name())
        .createdAt(train.getCreatedAt().toLocalDate())
        .build();
  }

}
