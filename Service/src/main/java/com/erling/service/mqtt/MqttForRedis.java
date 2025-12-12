package com.erling.service.mqtt;

import com.erling.service.obj.ServiceObject;
import com.erling.service.redis.ser.RedisDeviceInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class MqttForRedis extends ServiceObject {

    private final RedisDeviceInfoService  redisDeviceInfoService;

    @Autowired
    public MqttForRedis(RedisDeviceInfoService redisDeviceInfoService, MessageChannel mqttOutboundChannel) {
        this.redisDeviceInfoService = redisDeviceInfoService;
    }

    @ServiceActivator(inputChannel = "mqttInputChannel")
    public void handleIncomingMessage(Message<?> message) {
        String topic = message.getHeaders().get("mqtt_receivedTopic", String.class);
        if (topic != null) {
            List<String> topicList = Arrays.asList(topic.split("/"));
            if(topicList.getLast().equals("urgent")){
                StringBuilder topic_save= new StringBuilder();
                for(String item : topicList){
                    if(item.equals("urgent")){
                        continue;
                    }
                    topic_save.append(item).append("/");
                }
                topic_save.deleteCharAt(topic_save.length()-1);
                redisDeviceInfoService.setDeviceUrgent(topic_save.toString(), true);
            }

        }


    }

}
