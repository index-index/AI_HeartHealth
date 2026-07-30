package com.gym.ai_hearthealth.common;

import lombok.Data;

@Data
public class Result<T> {
    private String code;
    private String msg;
    private T data;

    public static <T> Result<T> Ok(){
        Result<T> result = new Result<>();
        result.setCode(ResultCode.SUCCESS.getCode());
        result.setMsg(ResultCode.SUCCESS.getMsg());
        return result;
    }

    //方法重载
    public static <T> Result<T> Ok(T data){
        Result<T> result = Ok();
        result.setData(data);
        return result;
    }

    public static <T> Result<T> Error(){
        Result<T> result = new Result<>();
        result.setCode(ResultCode.ERROR.getCode());
        result.setMsg(ResultCode.ERROR.getMsg() );
        return result;
    }
    public static <T> Result<T> Error(String code, String msg, T data){
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }
}
