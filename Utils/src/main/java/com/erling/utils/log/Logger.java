package com.erling.utils.log;

import org.slf4j.LoggerFactory;

public class Logger {

   public static class log{

        private org.slf4j.Logger logger;

        boolean isLog;

        public log setLogger(org.slf4j.Logger logger){
            this.logger = logger;
            return this;
        }
        public log setLogIsEnable(boolean isLog){

            this.isLog = isLog;
            return this;
        }

        public log info(String msg, Object... params){
            if(isLog){
                logger.info(msg, params);
            }
            return this;
        }
        public log debug(String msg, Object... params){
            if(isLog){
                logger.debug(msg, params);
            }
            return this;
        }
        public log error(String msg, Object... params){
            if(isLog){
                logger.error(msg, params);
            }
            return this;
        }
        public log warn(String msg, Object... params){
            if(isLog){
                logger.warn(msg, params);
            }
            return this;
        }
        public log trace(String msg, Object... params){
            if(isLog){
                logger.trace(msg, params);
            }
            return this;
        }

    }

    public static org.slf4j.Logger getLogger(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }
    public static log createLog(){
       return new log();
    }



}
