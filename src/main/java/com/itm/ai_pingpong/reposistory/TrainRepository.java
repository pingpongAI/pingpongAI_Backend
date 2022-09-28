package com.itm.ai_pingpong.reposistory;

import com.itm.ai_pingpong.domain.Member;
import com.itm.ai_pingpong.domain.Train;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainRepository extends JpaRepository<Train, Long> {

  List<Train> findAllByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

  List<Train> findTrainsByMember(Member member);

}
