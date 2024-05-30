package com.boardtest.board.exception;

import lombok.Getter;

// 실행 환경에서 발생하는 예외처리를 위함
public class BusinessException extends RuntimeException{
    @Getter
    private ExceptionCode exceptionCode;
    public BusinessException(ExceptionCode exceptionCode){
        super(exceptionCode.getMessage());
        this.exceptionCode = exceptionCode;
    }
}
