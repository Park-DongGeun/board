package com.boardtest.board.answer;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AnswerMapper {
    public AnswerDto.AnswerResponseDto answerToAnswerResponseDto(Answer answer);
}
