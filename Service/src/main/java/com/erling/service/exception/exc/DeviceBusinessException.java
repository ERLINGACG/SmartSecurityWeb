package com.erling.service.exception.exc;

import com.erling.service.exception.exc.base.BaseBusinessException;
import com.erling.utils.result.inf.CodeMessage;
import lombok.Getter;

@Getter
public class DeviceBusinessException extends BaseBusinessException {

    public DeviceBusinessException(CodeMessage codeMessage) {
        super(codeMessage);
    }



}
