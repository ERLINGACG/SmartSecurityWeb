package com.erling.service.exception.aop.inf;

import com.erling.service.obj.ServiceObject;

public class BaseExceptionAspect extends ServiceObject {
    public void getThreadInfo() {
        this.log.info("进程ID:{}", ProcessHandle.current().pid());
        this.log.info("线程ID:{}", Thread.currentThread().threadId());
        this.log.info("线程名称:{}", Thread.currentThread().getName());
        this.log.info("线程状态:{}", Thread.currentThread().getState());
    }

    public void getFilePosInfo(Exception exception) {
        this.log.info(  "来自类名:{}", exception.getStackTrace()[0].getClassName());
        this.log.error( "异常名称:{}", exception.getClass().getSimpleName());
        this.log.error( "异常行号:{}", exception.getStackTrace()[0].getLineNumber());
        this.log.error( "来自方法:{}", exception.getStackTrace()[0].getMethodName());
    }
    public void getStackTraceInfo(Exception exception) {
        this.log.error("堆栈信息:");
        // 只打印项目内（com.erling包）的堆栈
        for (StackTraceElement stackTraceElement : exception.getStackTrace()) {
            if (stackTraceElement.getClassName().startsWith("com.erling")) {
                this.log.error("{}", stackTraceElement);
            }
        }
    }

    public void getInfo(Exception e){
        this.getThreadInfo();
        this.getFilePosInfo(e);
        this.getStackTraceInfo(e);
    }



}
