package com.itm.ai_pingpong.reposistory;

import com.itm.ai_pingpong.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByEmail(String name);

  boolean existsByEmail(String email);
}
