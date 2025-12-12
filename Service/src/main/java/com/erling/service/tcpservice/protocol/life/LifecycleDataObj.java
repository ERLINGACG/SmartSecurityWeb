package com.erling.service.tcpservice.protocol.life;

import com.erling.service.detectHistroy.DetectionHistoryService;
import com.erling.service.mqtt.MqttService;
import com.erling.service.opencv.model.yolo.YoloV5;
import com.erling.service.redis.ser.RedisDeviceInfoService;
import com.erling.service.redis.ser.RedisZSetService;
import com.erling.service.tcpservice.protocol.conn.ObjConn;
import com.erling.service.tcpservice.protocol.data.DataQueue;
import lombok.Getter;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

@SuppressWarnings("unchecked")
public class LifecycleDataObj<T> implements LifecycleDataInf<T> {
    @Getter
    List<ObjConn<? extends ObjConn<?>>> ConnItem = new ArrayList<>();

    @Getter
    public String connKey;

    @Getter
    public DataQueue dataQueue;

    @Getter
    protected Socket clientSocket;

    @Getter
    public Long timeOut;


    @Getter
    public YoloV5 yoloV5;



    @Getter
    public MqttService mqttService;

    @Getter
    public RedisZSetService redisZSetService;

    @Getter
    public RedisDeviceInfoService redisDeviceInfoService;



    @Getter
    public DetectionHistoryService detectionHistoryService;


    @Getter
    public SimpMessagingTemplate template;

    @Getter
    boolean enable = true;

    @Getter
    ThreadPoolExecutor clientThreadPool;

    public T setClientThreadPool(ThreadPoolExecutor clientThreadPool) {
        this.clientThreadPool = clientThreadPool;
        return (T) this;
    }

    @Override
    public T setMqttService(MqttService mqttService) {
        this.mqttService = mqttService;
        return (T) this;
    }

    @Override
    public T setDetectionHistoryService(DetectionHistoryService detectionHistoryService) {
        this.detectionHistoryService = detectionHistoryService;
        return (T) this;
    }

    @Override
    public T setConnKey(String connKey) {
        this.connKey = connKey;
        return (T) this;
    }

    @Override
    public T setTimeout(int timeOut) {
        this.timeOut = (long) timeOut;
        return (T) this;
    }

    @Override
    public T setYoloV5(YoloV5 yoloV5) {
        this.yoloV5 = yoloV5;
        return (T) this;
    }

    @Override
    public T setRedisZSetService(RedisZSetService redisZSetService) {
        this.redisZSetService = redisZSetService;
        return (T) this;
    }

    @Override
    public T setTemplate(SimpMessagingTemplate template) {
        this.template = template;
        return (T) this;
    }

    @Override
    public T setClientSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
        return (T) this;
    }

    @Override
    public T setDataQueue(DataQueue dataQueue) {
        this.dataQueue = dataQueue;
        return (T) this;
    }

    @Override
    public T setLogEnable(boolean enable) {
        this.enable = enable;
        return (T) this;
    }


    public T setRedisDeviceInfoService(RedisDeviceInfoService redisDeviceInfoService) {
        this.redisDeviceInfoService = redisDeviceInfoService;
        return (T) this;
    }
}
