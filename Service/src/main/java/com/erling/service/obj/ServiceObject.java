package com.erling.service.obj;


import com.erling.service.config.DebugConfig;
import com.erling.service.exception.exc.base.BaseBusinessException;
import com.erling.utils.log.Logger;
import com.erling.utils.result.Result;
import com.erling.utils.result.ren.ServiceResultEnum;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;

import java.util.Objects;


//@Component
public class ServiceObject  {

    protected final org.slf4j.Logger logger =  Logger.getLogger(this.getClass());

    protected Logger.log log ;

    @Autowired
    protected DebugConfig debugConfig;

    protected Class<?> tClass;
    protected ServiceObject() {
        this.tClass = this.getClass();

    }

    public void validate(BindingResult result) {
        if(result.hasErrors()){
            throw new BaseBusinessException(
                    new Result<>(
                            ServiceResultEnum.PARAM_ERROR.getCode(),
                            Objects.requireNonNull(result.getFieldError()).getDefaultMessage(),
                            null
                    )
            );
        }
    }
    @PostConstruct
    protected void init(){
        this.log  = Logger.createLog().
                                setLogger(logger).
                                setLogIsEnable(
                                        debugConfig.isLog()
                                );
        log.info("init log for {}",tClass.getName());
    }

    @PreDestroy
    protected void destroy(){
        log.info("destroy {}",tClass.getName());
    }

    public void Reports(Object... object){}



}
