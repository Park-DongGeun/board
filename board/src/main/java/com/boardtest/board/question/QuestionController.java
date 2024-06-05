package com.boardtest.board.question;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/question")
@RequiredArgsConstructor
public class QuestionController {
    private final QuestionService questionService;
    // 단일 조회(상세 조회 -> Question 및 Answer)
    // 전체 조회(게시판 리스트 출력)

    // 질문 등록
    @PostMapping("/{member-id}")
    public ResponseEntity postQuestion(@PathVariable("member-id") Long memberId,
                             @Valid @RequestBody QuestionDto.QuestionPostDto dto){
        dto.setMemberId(memberId);
        questionService.createQuestion(dto);

        return new ResponseEntity(HttpStatus.CREATED);
    }
    // 질문 삭제

    @DeleteMapping("/{question-id}")
    public ResponseEntity deleteQuestion(@PathVariable("question-id") Long questionId){
        // 삭제 시 실제 DB 에서 삭제 되는 것이 아닌 상태값만 변경
        // 그러면 변경된 값을 Client 에 알려야 하지 않을까?
        // 그러면 question 객체를 리턴 받아 DTO 변환 후 ResponseEntity 에 담아 보내야할까?
        questionService.removeQuestion(questionId);

        return new ResponseEntity(HttpStatus.OK);
    }
    // 질문 수정
}
