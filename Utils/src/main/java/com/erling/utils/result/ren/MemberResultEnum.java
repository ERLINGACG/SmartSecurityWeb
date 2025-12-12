package com.erling.utils.result.ren;

import com.erling.utils.result.inf.CodeMessage;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum MemberResultEnum implements CodeMessage {


    MEMBER_ADD_SUCCESS(HttpStatus.OK.value(), "添加成功"),

    MEMBER_DETECT_NOT_FACE(HttpStatus.NOT_FOUND.value(), "未检测到人脸"),


    MEMBER_IS_EXIST(HttpStatus.BAD_REQUEST.value(), "成员已存在"),

    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "成员不存在"),

    MEMBER_DELETE_SUCCESS(HttpStatus.NO_CONTENT.value(), "删除成功"),

    MEMBER_UPDATE_SUCCESS(HttpStatus.NO_CONTENT.value(), "更新成功"),
    MEMBER_GET_SUCCESS(HttpStatus.OK.value(), "获取成功");



    public final int code;


    public final String message;

    MemberResultEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
