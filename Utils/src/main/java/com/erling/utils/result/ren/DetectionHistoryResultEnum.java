package com.erling.utils.result.ren;

import com.erling.utils.result.inf.CodeMessage;
import lombok.Getter;
import org.springframework.http.HttpStatus;

public enum DetectionHistoryResultEnum implements CodeMessage {





    DETECTION_HISTORY_SUCCESS(200,"检测历史查询成功"),

    DETECTION_HISTORY_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "检测历史不存在"),


    ;



    @Getter
    public final int code;

    @Getter
    public final String message;

    DetectionHistoryResultEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
