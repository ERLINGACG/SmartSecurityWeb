package com.erling.service.exception.aop;

import com.erling.service.exception.aop.inf.BaseExceptionAspect;
import com.erling.service.exception.exc.GroupBusinessException;
import com.erling.utils.result.ren.GroupResultEnum;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class GroupExceptionAspect extends BaseExceptionAspect {



        @Around("execution(* com.erling.service.group.GroupService.*(..))")
        public Object logDeviceExceptions(ProceedingJoinPoint pjp) throws Throwable {
            try {
                return pjp.proceed();
            }
            catch (DuplicateKeyException e){
                this.getInfo(e);
                throw new GroupBusinessException(GroupResultEnum.GROUP_IS_EXIST);
            }
            catch (Exception e){

                this.getInfo(e);
                throw  e;
            }

        }
}
