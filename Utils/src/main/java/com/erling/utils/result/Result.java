package com.erling.utils.result;

import com.erling.utils.result.inf.CodeMessage;
import com.erling.utils.result.ren.ServiceResultEnum;
import com.erling.utils.result.ren.UserResultEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Result<T> implements CodeMessage{

    private int code;
    private String message;
    private T data;

    public Result(CodeMessage e, T data)  {
        this.code = e.getCode();
        this.message = e.getMessage();
        this.data = data;
    }

    public Result(ResultEnum e,T data){
        this.code = e.getCode();
        this.message = e.getMessage();
        this.data = data;
    }
//
    public Result(UserResultEnum e, T data){
        this.code = e.getCode();
        this.message = e.getMessage();
        this.data = data;
    }
//
    public Result(ServiceResultEnum e, T data){
        this.code = e.getCode();
        this.message = e.getMessage();
        this.data = data;
    }


}
