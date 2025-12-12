package com.erling.service.exception.aop;

import com.erling.service.exception.aop.inf.BaseExceptionAspect;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class UserExceptionAspect extends BaseExceptionAspect {
    @Around("execution(* com.erling.service.user.ser.UserService.*(..))")
    public Object logDeviceExceptions(ProceedingJoinPoint pjp) throws Throwable {
        try {
            return pjp.proceed();
        }
        catch (Exception e){

            this.getInfo(e);
            throw  e;
        }

    }
}
