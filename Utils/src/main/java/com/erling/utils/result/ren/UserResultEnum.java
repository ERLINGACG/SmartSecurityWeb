package com.erling.utils.result.ren;

import com.erling.utils.result.inf.CodeMessage;
import lombok.Getter;
import org.springframework.http.HttpStatus;

public enum UserResultEnum implements CodeMessage {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "用户未注册"),

    USER_EMPTY(HttpStatus.BAD_REQUEST.value(), "用户信息为空"),

    USER_ALREADY_EXIST(HttpStatus.BAD_REQUEST.value(), "用户已存在"),

    USER_LOGIN_FAILED(HttpStatus.UNAUTHORIZED.value(), "用户名或密码错误"),

    USER_LOGIN_SUCCESS(HttpStatus.OK.value(), "登录成功"),

    USER_REGISTER_SUCCESS(HttpStatus.OK.value(), "用户注册成功"),
    USER_REGISTER_FAILED(HttpStatus.BAD_REQUEST.value(), "用户注册失败"),


    USER_DETAIL_SUCCESS(HttpStatus.OK.value(), "用户详情获取成功"),

    USER_UPDATE_SUCCESS(HttpStatus.OK.value(), "用户信息更新成功"),

    USER_UPDATE_FAILED(HttpStatus.BAD_REQUEST.value(), "用户信息更新失败"),

    USER_DELETE_SUCCESS(HttpStatus.OK.value(), "用户删除成功"),

    USER_DELETE_FAILED(HttpStatus.BAD_REQUEST.value(), "用户删除失败"),

    USER_GET_AVATAR_SUCCESS(HttpStatus.OK.value(), "用户头像获取成功"),

    USER_GET_AVATAR_FAILED(HttpStatus.BAD_REQUEST.value(), "用户头像获取失败"),
    ;


    @Getter
    public final int code;

    @Getter
    public final String message;

    UserResultEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
