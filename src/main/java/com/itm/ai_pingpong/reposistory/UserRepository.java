package com.itm.ai_pingpong.reposistory;

import com.itm.ai_pingpong.domain.User;
import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepository {

  private final EntityManager em;

  public void save(User user) {
    em.persist(user);
  }

  public User findOne(Long id) {
    return em.find(User.class, id);
  }

}
