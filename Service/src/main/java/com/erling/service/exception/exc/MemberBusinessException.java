package com.erling.service.exception.exc;

import com.erling.service.exception.exc.base.BaseBusinessException;
import com.erling.utils.result.inf.CodeMessage;

public class MemberBusinessException extends BaseBusinessException {
    public MemberBusinessException(CodeMessage codeMessage) {
        super(codeMessage);
    }
}
