package com.erling.service.exception.handler;

import com.erling.utils.result.Result;
import com.erling.utils.result.ren.ServiceResultEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<?>> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        // 提取具体错误信息
        String errorMsg = e.getBindingResult().getFieldErrors().getFirst().getDefaultMessage();

        // 返回统一格式的错误结果
        return ResponseEntity.
                status(ServiceResultEnum.PARAM_ERROR.getCode()).
                body(new Result<>(
                        ServiceResultEnum.PARAM_ERROR.getCode(), errorMsg,
                        null
                    )
                );
    }

    @ExceptionHandler(io.jsonwebtoken.security.SignatureException.class)
    public ResponseEntity<Result<?>> handleSignatureException(io.jsonwebtoken.security.SignatureException e) {
        log.error("签名异常: {}", e.getMessage(), e);
        return ResponseEntity.
                status(HttpStatus.UNAUTHORIZED).
                body(new Result<>(
                        ServiceResultEnum.UNAUTHORIZED,
                        e.getMessage()
                    )
                );
    }

}
