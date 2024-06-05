package com.boardtest.board.question;

import lombok.Getter;

@Getter
public enum QuestionStatus {
    QUESTION_REGISTERED,
    QUESTION_ANSWERED,
    QUESTION_DELETED,
    QUESTION_DEACTIVED;
}
