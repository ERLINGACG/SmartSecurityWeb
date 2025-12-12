package com.erling.service.tcpservice.protocol.life;

import com.erling.service.detectHistroy.DetectionHistoryService;
import com.erling.service.mqtt.MqttService;
import com.erling.service.opencv.model.yolo.YoloV5;
import com.erling.service.redis.ser.RedisZSetService;
import com.erling.service.tcpservice.protocol.data.DataQueue;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.concurrent.ThreadPoolExecutor;

public interface LifecycleDataInf<T> {

   T setConnKey(String connKey);

   T setTimeout(int timeOut);

   T setYoloV5(YoloV5 yoloV5);

   T setRedisZSetService(RedisZSetService redisZSetService);

   T setTemplate(SimpMessagingTemplate template);

   T setClientSocket(java.net.Socket clientSocket);

   T setDataQueue(DataQueue dataQueue);

   T setLogEnable(boolean enable);

   T setClientThreadPool(ThreadPoolExecutor clientThreadPool);

   T setMqttService(MqttService mqttService);

   T setDetectionHistoryService(DetectionHistoryService detectionHistoryService);
}
