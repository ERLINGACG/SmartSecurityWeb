package com.erling.service.tcpservice.protocol.conn;

import lombok.Getter;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Base64;
import java.util.Collections;
import java.util.Map;

public class SendWebSForConn extends ObjConn<SendWebSForConn> {
    @Getter
    private SimpMessagingTemplate template;

    public SendWebSForConn setTemplate(SimpMessagingTemplate template){
        this.template = template;
        return this;
    }
    public void SendByteAndTop(){
        try{
            while(!clientSocket.isClosed()){
                synchronized (dataQueue.getSendQueue()){
                    if(dataQueue.getSendQueue().isEmpty()){
                        dataQueue.getSendQueue().wait(100);
                    }else{
                        Map<String, byte[]> message = dataQueue.getSendQueue().poll();
                        if(message != null){
                            for(Map.Entry<String, byte[]> entry : message.entrySet()){
                                template.convertAndSend(
                                        entry.getKey() + "/image",
                                        Collections.singletonMap("image", Base64.getEncoder().
                                                encodeToString(
                                                        entry.getValue()
                                                )
                                        )
                                );
                            }
                            log.info("已发送图片到前端,topic:{}",message.keySet().iterator().next());
                        }
                    }

                }
            }
        }catch (Exception e){
            log.info("会话断开,已销毁发送线程,异常信息:{}",e);
        }
    }
}
