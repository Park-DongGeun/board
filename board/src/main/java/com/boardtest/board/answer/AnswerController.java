package com.boardtest.board.answer;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/answer")
@RequiredArgsConstructor
public class AnswerController {
    private final AnswerService answerService;
    private final AnswerMapper answerMapper;
    @PostMapping
    public ResponseEntity postAnswer(@RequestBody @Valid AnswerDto.AnswerPostDto dto){
        answerService.replyAnswer(dto);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping
    public ResponseEntity deleteAnswer(@RequestBody @Valid AnswerDto.AnswerDeleteDto dto){
        answerService.removeAnswer(dto);

        return new ResponseEntity(HttpStatus.OK);
    }

    @PatchMapping
    public ResponseEntity modifyAnswer(@RequestBody @Valid AnswerDto.AnswerModifyDto dto){
        AnswerDto.AnswerResponseDto responseDto = answerMapper.answerToAnswerResponseDto(answerService.modify(dto));

        return new ResponseEntity(responseDto, HttpStatus.OK);
    }
}
