package com.ssafy.lumiroom.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * @Valid 어노테이션으로 인한 유효성 검사 실패 시 이 메서드가 가로챕니다.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        
        BindingResult bindingResult = ex.getBindingResult();
        
        // 발생한 여러 유효성 검사 에러 중, 가장 첫 번째 에러의 메시지만 추출합니다.
        // 예: "이메일은 필수 입력값입니다.", "비밀번호는 8~20자..." 등
        String errorMessage = bindingResult.getFieldErrors().get(0).getDefaultMessage();

        // 추출한 한글 메시지를 400 Bad Request 상태와 함께 순수 문자열(String)로 반환합니다.
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }
}
