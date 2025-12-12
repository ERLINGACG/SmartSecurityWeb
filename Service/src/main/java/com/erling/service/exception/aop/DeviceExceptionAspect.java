package com.erling.service.exception.aop;

import com.erling.service.exception.aop.inf.BaseExceptionAspect;
import com.erling.service.exception.exc.DeviceBusinessException;
import com.erling.utils.result.ren.DeviceResultEnum;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class DeviceExceptionAspect extends BaseExceptionAspect  {


    @Around("execution(* com.erling.service.device.DeviceService.*(..))")
    public Object logDeviceExceptions(ProceedingJoinPoint pjp) throws Throwable {
        try {
            return pjp.proceed();
        }
        catch (DuplicateKeyException e){
            this.getInfo(e);
            throw new DeviceBusinessException(
                    DeviceResultEnum.DEVICE_IS_EXIST
            );
        }
        catch (Exception e){

             this.getInfo(e);
             throw  e;
        }

    }

}
