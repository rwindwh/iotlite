package com.dj.iotlite.handler;


import com.dj.iotlite.api.dto.ResDto;
import com.dj.iotlite.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;


@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    @ExceptionHandler(value = BusinessException.class)
    @ResponseBody
    public ResDto handlerBusinessException(BusinessException ex) {
        return ResUtils.fail(ex.getCode(), ex.getMessage(), ex.getData());
    }

    /**
     * TODO 写入日志
     *
     * @param
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResDto exceptionGet(Exception e) {
        e.printStackTrace();
        return ResUtils.fail(-1, e.getMessage(),"");
    }


}
