package com.erling.utils.result.ren;

import com.erling.utils.result.inf.CodeMessage;
import lombok.Getter;
import org.springframework.http.HttpStatus;

public enum DeviceResultEnum implements CodeMessage{



    ADD_DEVICE_SUCCESS(
            HttpStatus.OK.value(),
            "添加设备成功"
    ),


    ADD_DEVICE_FAILURE(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "添加设备失败"
    ),
    UPDATE_DEVICE_SUCCESS(
            HttpStatus.OK.value(),
            "更新设备成功"
    ),
    UPDATE_DEVICE_FAILURE(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "更新设备失败"
    ),
    UPLOAD_DEVICE_EXISTS(
            HttpStatus.BAD_REQUEST.value(),
            "设备键重复"
    ),
    DELETE_DEVICE_SUCCESS(
            HttpStatus.OK.value(),
            "删除设备成功"
    ),
    DELETE_DEVICE_FAILURE(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "删除设备失败"
    ),
    GET_DEVICE_SUCCESS(
            HttpStatus.OK.value(),
            "获取设备成功"
    ),
    SELECT_DEVICE_LIST_SUCCESS(
            HttpStatus.OK.value(),
            "获取设备列表成功"
    ),
    SELECT_DEVICE_LIST_FAILURE(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "获取设备列表失败"
    ),
    DEVICE_NOT_FOUND(
            HttpStatus.NOT_FOUND.value(),
            "设备不存在"
    ),
    DEVICE_IS_EXIST(
            HttpStatus.BAD_REQUEST.value(),
            "设备已存在"
    ),;



    @Getter
    public final int code;

    @Getter
    public final String message;

    DeviceResultEnum(int code, String message) {
        this.code = code;
        this.message = message;

    }

}
