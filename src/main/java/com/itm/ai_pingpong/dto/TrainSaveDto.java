package com.itm.ai_pingpong.dto;

import com.itm.ai_pingpong.domain.Train;
import com.itm.ai_pingpong.domain.TrainType;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public record TrainSaveDto(
    @NotBlank(message = "훈련타입을 입력해주세요")
    String type,
    @NotNull(message = "옳은 횟수를 입력해주세요")
    int rightCount,
    @NotNull(message = "틀린 횟수를 입력해주세요")
    int wrongCount
) {

  public Train toEntity() {
    return Train.builder()
        .type(TrainType.valueOf(type))
        .rightCount(rightCount)
        .wrongCount(wrongCount)
        .build();
  }
}
