package com.boardtest.board.advice;

import com.boardtest.board.exception.BusinessException;
import com.boardtest.board.exception.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// 컨트롤러에서 발생하는 에러를 잡는다.
// 여러 컨트롤러에 대한 전역적으로 ExceptionHandler 적용
// @Component 어노테인션을 통해 빈으로 등록
@RestControllerAdvice
public class GlobalExceptionAdvice {

    // 내가 설계한 BusinessException 예외가 발생하면 이 곳에서 잡는다.
    @ExceptionHandler
    public ResponseEntity handleBusinessLogicException(BusinessException e){
        final ErrorResponse errorResponse = ErrorResponse.of(e.getExceptionCode());

        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(e.getExceptionCode().getStatus()));
    }
}
