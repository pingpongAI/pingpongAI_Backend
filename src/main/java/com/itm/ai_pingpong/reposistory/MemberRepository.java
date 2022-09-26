package com.itm.ai_pingpong.reposistory;

import com.itm.ai_pingpong.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

  Member findByEmail(String email);

  boolean existsByEmail(String email);

}
