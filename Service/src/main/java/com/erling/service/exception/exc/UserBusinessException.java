package com.erling.service.exception.exc;

import com.erling.service.exception.exc.base.BaseBusinessException;
import com.erling.utils.result.inf.CodeMessage;

public class UserBusinessException extends BaseBusinessException {
    public UserBusinessException(CodeMessage codeMessage) {
        super(codeMessage);
    }
}
