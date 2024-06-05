package com.boardtest.board.answer;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class AnswerDto {
    @Getter
    @NoArgsConstructor
    public static class AnswerPostDto{
        @NotBlank
        Long memberId;
        @NotBlank
        Long questionId;
        @NotBlank
        String content;
    }

    @Getter
    @NoArgsConstructor
    public static class AnswerDeleteDto{
        @NotBlank
        Long memberId;

        @NotBlank
        Long answerId;
    }

    @Getter
    @NoArgsConstructor
    public static class AnswerModifyDto{
        @NotBlank
        Long memberId;

        @NotBlank
        Long answerId;

        @NotBlank
        String content;
    }

    @Getter
    @NoArgsConstructor
    public static class AnswerResponseDto{
        Long answerId;
        String content;
    }
}
