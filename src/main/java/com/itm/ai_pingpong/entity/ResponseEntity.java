package com.itm.ai_pingpong.entity;

import org.springframework.http.HttpEntity;

public class ResponseEntity extends HttpEntity {

  private final Object status;

  public ResponseEntity(Object status) {
    this.status = status;
  }
}
