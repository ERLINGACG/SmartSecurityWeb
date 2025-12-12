package com.erling.utils.result.ren;

import com.erling.utils.result.inf.CodeMessage;
import lombok.Getter;
import org.springframework.http.HttpStatus;

public enum GroupResultEnum implements CodeMessage {
    GROUP_IS_EXIST(HttpStatus.BAD_REQUEST.value(), "分组已存在"),
    GROUP_NOT_EXIST(HttpStatus.BAD_REQUEST.value(), "分组不存在"),
    ADD_GROUP_PARAM_ERROR(HttpStatus.BAD_REQUEST.value(), "参数错误"),
    SELECT_GROUP_SUCCESS(HttpStatus.OK.value(), "获取分组成功"),
    DELETE_GROUP_SUCCESS(HttpStatus.OK.value(), "分组删除成功"),
    UPDATE_GROUP_SUCCESS(HttpStatus.OK.value(), "分组更新成功");



    @Getter
    public final int code;

    @Getter
    public final String message;

    GroupResultEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
