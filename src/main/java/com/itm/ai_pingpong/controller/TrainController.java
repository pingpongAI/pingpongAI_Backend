package com.itm.ai_pingpong.controller;

import com.itm.ai_pingpong.dto.TrainResponseDto;
import com.itm.ai_pingpong.dto.TrainSaveDto;
import com.itm.ai_pingpong.service.TrainService;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/train")
public class TrainController {

  private final TrainService trainService;

  /**
   * 회원의 훈련 데이터를 가져오는 API
   *
   * @param id       회원의 id (FK)
   * @param pageable
   * @return Page<TrainResponseDto> 200 OK, 훈련 정보
   */
  @GetMapping("/{id}")
  public List<TrainResponseDto> find(@PathVariable("id") long id, Pageable pageable) {
    return trainService.findAll(id, pageable).getContent();
  }

  @PostMapping("/{id}")
  public ResponseEntity<TrainSaveDto> saveTrain(@PathVariable("id") long id,
      @Valid @RequestBody TrainSaveDto trainSaveDto) {
    return ResponseEntity.ok().body(trainService.saveTrain(id, trainSaveDto));
  }
}
