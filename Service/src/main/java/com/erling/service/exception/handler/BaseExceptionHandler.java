package com.erling.service.exception.handler;

import com.erling.service.exception.exc.base.BaseBusinessException;
import com.erling.service.obj.ServiceObject;
import com.erling.utils.result.Result;
import com.erling.utils.result.ren.ServiceResultEnum;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;


public  class BaseExceptionHandler extends ServiceObject {

    @ExceptionHandler
    public ResponseEntity<Result<Object>> handle(BaseBusinessException e) {
        this.log.error(e.getMessage(), e);
        return ResponseEntity.status(e.getCodeMessage().getCode())
                .body(new Result<>(
                        e.getCodeMessage(),
                        null
                ));
    }

    @ExceptionHandler
    public ResponseEntity<Result<Object>> handleException(Exception ex) {
        this.log.error(ex.getMessage(), ex);
        return ResponseEntity.status(500)
                .body(new Result<>(
                        ServiceResultEnum.FAILURE,
                        null
                ));
    }




}
