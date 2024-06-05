package com.boardtest.board.member;


import com.boardtest.board.question.Question;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

public class MemberDto {
    @Getter
    public static class MemberPostDto{
        @NotBlank
        private String email;
        @NotBlank
        // @Pattern
        private String password;
        @NotBlank
        private String userName;
    }

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
