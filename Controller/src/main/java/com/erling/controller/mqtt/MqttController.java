package com.erling.controller.mqtt;

import com.erling.service.mqtt.MqttService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mqtt")
public class MqttController {
    private final MqttService mqttService;
    @Autowired
    public MqttController(MqttService mqttService) {
        this.mqttService = mqttService;
    }
    @GetMapping("/send")
    public void send() {
        mqttService.sendToMqtt("hello world", "/topic/test");
    }
}
