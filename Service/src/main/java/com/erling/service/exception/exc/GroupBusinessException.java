package com.erling.service.exception.exc;

import com.erling.service.exception.exc.base.BaseBusinessException;
import com.erling.utils.result.inf.CodeMessage;

public class GroupBusinessException extends BaseBusinessException {
    public GroupBusinessException(CodeMessage codeMessage) {
        super(codeMessage);
    }
}
