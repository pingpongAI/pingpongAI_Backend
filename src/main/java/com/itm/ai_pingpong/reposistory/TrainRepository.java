package com.itm.ai_pingpong.reposistory;

import com.itm.ai_pingpong.domain.Train;
import com.itm.ai_pingpong.domain.User;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TrainRepository {

  private final EntityManager em;

  public void save(Train train) {
    em.persist(train);
  }

  public List<Train> findTrainSpecificDate(User user, LocalDate date) {
    List<Train> resultList = em.createQuery(
            "select t from Train t join  t.user u where u.id = :userId and t.startAt >= :startDate and t.startAt < :endDate",
            Train.class)
        .setParameter("userId", user.getId())
        .setParameter("startDate", date.atStartOfDay())
        .setParameter("endDate", date.atTime(LocalTime.MAX))
        .getResultList();
    return resultList;
  }

}
