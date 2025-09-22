package com.erling.service.mqtt;

import com.erling.utils.log.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class MqttService {
    private final MessageChannel mqttOutboundChannel;
    private final SimpMessagingTemplate template;
    private String topic;
    private String payload;

    @Autowired
    public MqttService(MessageChannel mqttOutboundChannel, SimpMessagingTemplate template) {
        this.mqttOutboundChannel = mqttOutboundChannel; // 注入MQTT输出通道
        this.template = template;
    }

    // 发送消息到指定主题
    public void sendToMqtt(String payload, String topic) {
        Logger.getLogger(MqttService.class).info("发送消息到主题:{},消息:{}",topic,payload);
        mqttOutboundChannel.send(MessageBuilder.withPayload(payload)
                .setHeader("mqtt_topic", topic).build());
    }
    // 接收消息处理（监听mqttInputChannel）
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public void handleIncomingMessage(Message<?> message) {
        String topic = message.getHeaders().get("mqtt_receivedTopic", String.class);
        String payload = message.getPayload().toString();
        template.convertAndSend(topic+"/message", payload);
        Logger.getLogger(MqttService.class).info("收到来自 [{}] 的消息: {}",topic,payload);

    }

}
