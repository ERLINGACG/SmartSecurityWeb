package com.erling.utils.result.ren;

import com.erling.utils.result.inf.CodeMessage;
import lombok.Getter;
import org.springframework.http.HttpStatus;

public enum VCodeEnum   implements CodeMessage {


    VERIFICATION_CODE_ERROR(HttpStatus.BAD_REQUEST.value(), "验证码错误"),
    VERIFICATION_CODE_SUCCESS(HttpStatus.OK.value(), "验证码校验成功"),
    ;
    @Getter
    public final int code;

    @Getter
    public final String message;

    VCodeEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
