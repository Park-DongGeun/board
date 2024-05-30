package com.boardtest.board.exception;

import lombok.Getter;

public enum ExceptionCode {

    DIFFERENT_PASSWORD(401, "Different Password")// 비밀번호 틀림
    ;

    @Getter
    private int status;
    @Getter
    private String message;

    ExceptionCode(int status, String message){
        this.status = status;
        this.message = message;
    }
}
