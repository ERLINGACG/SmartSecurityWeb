package com.erling.controller.hal;

import com.erling.service.mqtt.MqttService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/HAL/esp32")
public class Esp32Controller {

    private final MqttService mqttService;
    Esp32Controller(MqttService mqttService){

        this.mqttService = mqttService;
    }

    @RequestMapping("/send")
    public void send() {
        mqttService.sendToMqtt("hello world", "/topic/test");
    }
}
