package com.erling.utils.result.ren;

import com.erling.utils.result.inf.CodeMessage;
import lombok.Getter;
import org.springframework.http.HttpStatus;

public enum ServiceResultEnum implements CodeMessage {

    SUCCESS(HttpStatus.OK.value(), "操作成功"),
    FAILURE(HttpStatus.INTERNAL_SERVER_ERROR.value(), "服务器内部错误"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED.value(), "请登录后操作"),

    PARAM_ERROR(HttpStatus.BAD_REQUEST.value(), "参数错误"),
    ;

    @Getter
    public final int code;

    @Getter
    public final String message;

    ServiceResultEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
