package com.itm.ai_pingpong.global.exception;

public abstract class BaseException extends RuntimeException {

  public abstract BaseExceptionType getExceptionType();
//  에러코드와 Http상태, 그리고 에러 메세지를 가지고 있도록 만들어 주기 위해 getter 메서드를 설정

}
