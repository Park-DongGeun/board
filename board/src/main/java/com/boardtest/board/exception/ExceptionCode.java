package com.boardtest.board.exception;

import lombok.Getter;

public enum ExceptionCode {

    DIFFERENT_PASSWORD(401, "Different Password"), // 비밀번호 틀림
    EXIST_MEMBER(401, "Exist Member"),
    MEMBER_NOT_FOUND(401, "Member Not Found"),
    MEMBER_IS_NOT_ADMIN(401, "Member is Not Admin"),
    QUESTION_NOT_FOUND(401, "Question Not Found"),
    ANSWER_NOT_FOUND(401, "Answer Not Found")
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
