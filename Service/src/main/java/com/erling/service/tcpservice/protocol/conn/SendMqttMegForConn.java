package com.erling.service.tcpservice.protocol.conn;

import com.erling.service.mqtt.MqttService;
import com.erling.service.redis.ser.RedisZSetService;
import lombok.Getter;

import java.util.Map;
import java.util.Objects;

public class SendMqttMegForConn extends ObjConn<SendMqttMegForConn>{

    @Getter
    private MqttService  mqttService;

    @Getter
    private RedisZSetService redisZSetService;

    public SendMqttMegForConn setMqttService(MqttService mqttService){
        this.mqttService=mqttService;
        return this;
    }

    public SendMqttMegForConn setRedisZSetService(RedisZSetService redisZSetService){
        this.redisZSetService=redisZSetService;
        if (Objects.isNull(redisZSetService)){
            log.warn("redisZSetService is null");
        }
        return this;
    }


    public void SendMqttMeg(){
        StringBuilder resultStr = new StringBuilder();
        try{
            int count=0;
            while (!clientSocket.isClosed()){
                synchronized (dataQueue.getMessageQueue()){
                     if(dataQueue.getMessageQueue().isEmpty()){
                         dataQueue.getMessageQueue().wait(100);
                     }
                     Map<String,String> message=dataQueue.getMessageQueue().poll();
                     count++;
                     boolean isSuccess;
                     if (message != null ) {
                        String JsonResult =message.values().iterator().next();
                        String topic      =message.keySet().iterator().next();

                        if(!Objects.equals(JsonResult, "" ) ){
                            String preStr ="\""+System.currentTimeMillis()+"\":" + "["+JsonResult+"]"+",";
                            resultStr.append(preStr);
                        }else{
                            log.info("第{}条消息,JsonResult为空",count);
                        }

                        if(count%10==0){ // 每10条消息发送一次,避免刷屏
                            String tempJson = "{" + resultStr + "}";
                            // 检查并删除末尾逗号（即倒数第二位的逗号，因为最后一位是}）
                            if (tempJson.charAt(tempJson.length() - 2) == ',') {
                                // 截取到倒数第二位前，再拼接}
                                tempJson = tempJson.substring(0, tempJson.length() - 2) + "}";
                            }
                            // 最终的mqttJson
                            String mqttJson = tempJson;

                            isSuccess=redisZSetService.addDetectionResult_0(topic,mqttJson);
                            mqttService.sendToMqtt(mqttJson,topic);
                            log.info("第{}条消息:{},存储结果:{}",count,mqttJson,isSuccess);

                            // 核心修复：发送后清空缓冲区，避免数据累积
                            resultStr.setLength(0);
                        }


                    }
                }
            }
        }catch (Exception e){
            log.info("发送Mqtt普通消息异常:{}",e.getMessage());
        }

    }
}
