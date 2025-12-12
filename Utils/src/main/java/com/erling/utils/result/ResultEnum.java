package com.erling.utils.result;

import lombok.Getter;

@Getter
public enum ResultEnum {

    // 成功类
    SUCCESS(200, "操作成功"),
    // 客户端错误类
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "身份验证失败"),
    FORBIDDEN(403, "访问权限不足"),
    NOT_FOUND(404, "资源未找到"),
    CONFLICT(409, "资源冲突"),

    // 服务端错误类
    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),

    // 业务验证类
    VALIDATION_ERROR(4001, "参数验证失败"),  // 原401改为4位业务码
    LOGIN_EXPIRED(4011, "登录状态已过期"),  // 原401改为4位业务码
    CAPTCHA_ERROR(4002, "验证码错误"),

    USER_NOT_EXIST(1001, "用户不存在"),  // 原101改为4位业务码
    WRONG_PASSWORD(1002, "密码错误"),    // 原102改为4位业务码
    EMAIL_REGISTERED(1003, "邮箱已注册"), // 原103改为4位业务码
    LOGIN_SUCCESS(2000, "登录成功"), // 原200改为4位业务码
    REGISTER_SUCCESS(2001, "注册成功"),

    DEVICE_NOT_EXIST(3001, "设备不存在"),
    DEVICE_EXIST(3002, "设备已存在"),
    DEVICE_ADD_SUCCESS(2002, "设备添加成功"),
    DEVICE_SELECT_SUCCESS(2003, "设备查询成功"), // 原200改为4位业务码
    DEVICE_SELECT_FAIL(4004, "设备查询失败"),
    DEVICE_DELETE_SUCCESS(2003, "设备删除成功"),

    DEVICE_UPDATE_SUCCESS(2005, "设备更新成功"),


    GROUP_NOT_EXIST(4001, "分组不存在"),
    GROUP_EXIST(4002, "分组已存在"),
    GROUP_ADD_SUCCESS(2006, "分组添加成功"),
    GROUP_ADD_FAIL(4003, "分组添加失败"),
    GROUP_SELECT_SUCCESS(200, "分组查询成功"), // 原200改为4位业务码
    GROUP_SELECT_FAIL(4005, "分组查询失败"),
    GROUP_DELETE_SUCCESS(2007, "分组删除成功"),
    GROUP_DELETE_FAIL(4007, "分组删除失败"),
    GROUP_UPDATE_SUCCESS(2008, "分组更新成功"),
    GROUP_UPDATE_FAIL(4009, "分组更新失败"),


    MEMBER_NOT_EXIST(4001, "成员不存在"),
    MEMBER_EXIST(4002, "成员已存在"),
    MEMBER_ADD_SUCCESS(2009, "成员添加成功"),
    MEMBER_ADD_FAIL(4003, "成员添加失败"),
    MEMBER_SELECT_SUCCESS(2004, "成员查询成功"), // 原200改为4位业务码
    MEMBER_SELECT_FAIL(4005, "成员查询失败"),
    MEMBER_DELETE_SUCCESS(2007, "成员删除成功"),
    MEMBER_DELETE_FAIL(4007, "成员删除失败"),
    MEMBER_UPDATE_SUCCESS(2008, "成员更新成功"),
    MEMBER_UPDATE_FAIL(4009, "成员更新失败"),
    MEMBER_VERIFY_SUCCESS(2010, "成员验证成功"),
    MEMBER_VERIFY_FAIL(4010, "成员验证失败"),
    MEMBER_VERIFY_FACES_ISNULL(4013, "未检测到人脸"),
    MEMBER_VERIFY_DISTANCE_HIGH(4011, "成员验证距离过高"),
    MEMBER_VERIFY_GROUP_ISNULL(4012, "成员验证分组为空"),

    ; // 原200改为4位业务码



    private final int code;
    private final String message;

    ResultEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
