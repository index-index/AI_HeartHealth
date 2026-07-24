package com.gym.ai_hearthealth.common;


import com.gym.ai_hearthealth.exception.BusinessException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

import static com.gym.ai_hearthealth.common.ResultCode.PARAM_ERROR;

@RestControllerAdvice
public class GlobarExceptionHandler {
    //处理参数校验异常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<String> handlerException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(","));
        return Result.Error(ResultCode.PARAM_ERROR.getCode(), PARAM_ERROR.getMsg(), message);
    }

    //处理业务异常
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e) {
        //如果异常携带额外的数据
        if (e.getData() != null){
            return Result.Error(e.getCode(), e.getMessage(), e.getData());
        }
        return Result.Error(e.getCode(), e.getMessage(), null);
    }

}
