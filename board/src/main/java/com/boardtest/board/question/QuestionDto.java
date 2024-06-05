package com.boardtest.board.question;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class QuestionDto {

    @Getter
    @NoArgsConstructor
    public static class QuestionPostDto{
        // URI 로 날려보냄
        @Setter
        private Long memberId;
        @NotBlank
        private String title;
        @NotBlank
        private String content;
        private boolean isPrivate;
    }
}
