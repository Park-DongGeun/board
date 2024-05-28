package com.boardtest.board.member;


import com.boardtest.board.question.Question;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

public class MemberDto {

    @Getter
    @Setter
    @AllArgsConstructor
    public class MemberResponseDto{
        private Long memberId;
        private List<Question> questionList;
        private String userName;
    }

    public interface MemberResponseDtoVerInterface{
        Long getMemberId();
        List<Question> getQuestionList();
        String getUserName();
    }
}
