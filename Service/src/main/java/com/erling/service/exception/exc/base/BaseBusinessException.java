package com.erling.service.exception.exc.base;

import com.erling.utils.result.inf.CodeMessage;
import lombok.Getter;

@Getter
public class BaseBusinessException extends RuntimeException {


    public final CodeMessage codeMessage;


    public BaseBusinessException(CodeMessage codeMessage){
        super(codeMessage.getMessage());
        this.codeMessage = codeMessage;

    }


}
