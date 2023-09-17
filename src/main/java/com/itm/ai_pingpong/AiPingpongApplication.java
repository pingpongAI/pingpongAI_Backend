package com.itm.ai_pingpong;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class AiPingpongApplication {
  public static void main(String[] args) {
    SpringApplication.run(AiPingpongApplication.class, args);

  }
}
