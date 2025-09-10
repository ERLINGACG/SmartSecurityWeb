package com.erling.service.tcpservice.protocol;

import com.erling.service.mqtt.MqttService;
import com.erling.service.opencv.dnn.YoloDnnTest;
import com.erling.service.redis.ser.RedisZSetService;
import com.erling.utils.log.Logger;
import com.erling.utils.pattern.PatternUtils;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.util.*;

public class TcpProtocol {
    private final Queue<Map<String, byte[]>> getQueue = new LinkedList<>();

    private final Queue<Map<String, byte[]>> sendQueue = new LinkedList<>();

    private final Queue<Map<String, String>> messageQueue = new LinkedList<>();

    private final Queue<Map<String, String>> urgentQueue = new LinkedList<>();

    private final MqttService mqttService;

    private final RedisZSetService  redisZSetService;
    private final YoloDnnTest yoloDnnTest;

    @Getter
    private volatile boolean isConnected = true;


    int count = 0;
    public TcpProtocol( YoloDnnTest yoloDnnTest,RedisZSetService redisZSetService,MqttService mqttService) {
        this.mqttService = mqttService;
        this.redisZSetService = redisZSetService;
        this.yoloDnnTest = yoloDnnTest;
    }

    public int getQueueSize(){
        return sendQueue.size();
    }

    public int getSendQueueSize(){
        return sendQueue.size();
    }

    public void saveRedis_0(String key, String value) {
            redisZSetService.addDetectionResult_0(key, value);
    }

    public  void processIO(Socket clientSocket) throws IOException {
        try(DataInputStream input = new DataInputStream(clientSocket.getInputStream())){
            long lastActiveTime = System.currentTimeMillis();
            while (!clientSocket.isClosed()) {
                long startTime = System.currentTimeMillis();
                Logger.getLogger(TcpProtocol.class).info("当前客户端会话:{}",clientSocket.getInetAddress());
                try{
                    byte[] header = new byte[40];
                    input.readFully(header);
                    lastActiveTime = System.currentTimeMillis(); // 重置活跃时间
                    ByteBuffer buffer = ByteBuffer.wrap(header).order(ByteOrder.LITTLE_ENDIAN);
                    byte[] topicBytes = new byte[32];
                    buffer.get(topicBytes);  // 读取32字节topic
                    int topicLength = buffer.getInt();  // 读取4字节topic长度
                    int bodyLength = buffer.getInt();   // 读取4字节消息体长度
                    // 验证topic长度有效性
                    if (topicLength < 0 || topicLength > 32) {
                        throw new IOException("无效的topic长度: " + topicLength);
                    }
                    String topic = new String(topicBytes, 0, topicLength, StandardCharsets.UTF_8);
                    // 分块读取消息体（保持与TcpImageService相同的读取逻辑）
                    ByteArrayOutputStream messageBuffer = new ByteArrayOutputStream(bodyLength);
                    byte[] bodyData = new byte[bodyLength];
                    input.readFully(bodyData);

                    synchronized (getQueue) {
                        getQueue.offer(Map.of(topic, bodyData)); // 加入消息队列
                        getQueue.notifyAll(); // 通知处理线程有新的数据
                        Logger.getLogger(TcpProtocol.class).info("收到消息: {}, 长度: {}", topic, bodyLength);
                        Logger.getLogger(TcpProtocol.class).info("队列长度: {}", getQueue.size());
                        long endTime = System.currentTimeMillis();
                        Logger.getLogger(TcpProtocol.class).info("收到消息耗时: {}ms", endTime - startTime);
                    }

                }catch (SocketTimeoutException e){
                    if (System.currentTimeMillis() - lastActiveTime >= 30000) {
                        Logger.getLogger(TcpProtocol.class).info("未收到数据30秒，即将断开连接");
                        clientSocket.close();
                        isConnected = false;
                        break;
                    }
                }catch (IOException e){
                    clientSocket.close();
                    isConnected = false;
                    Logger.getLogger(TcpProtocol.class).error("TCP协议处理异常：{}，断开连接：{}",e,isConnected);
                    break;
                }
            }
        }catch(EOFException e){
            Logger.getLogger(TcpProtocol.class).info("客户端主动断开连接",e);

        }finally {
            Logger.getLogger(TcpProtocol.class).info("客户端会话已断开isConnected:{}",isConnected);
        }
    }

    public void ProcessMessage(Socket clientSocket) throws IOException {
        try{
        StringBuilder resultStr = new StringBuilder();
        while(!clientSocket.isClosed() && isConnected){
            try {
                long startTime = System.currentTimeMillis();
                synchronized (getQueue) {
                    if (getQueue.isEmpty()) {
                        getQueue.wait(100); // 等待新消息，超时时间为500ms

                    }
                    Map<String, byte[]> message = getQueue.poll(); // 取出消息
                    count++;
                    if (message != null) {

                        for (Map.Entry<String, byte[]> entry : message.entrySet()) {
                            Map<String, byte[]> resultMap = yoloDnnTest.TEST_D3(entry.getValue());

                            byte[] result = resultMap.values().iterator().next();
                            String JsonResult =resultMap.keySet().iterator().next();
                            if(!Objects.equals(JsonResult, "" ) ){
                                String preStr ="\""+System.currentTimeMillis()+"\":" + "["+JsonResult+"]"+",";
                                resultStr.append(preStr);
                            }
                            Logger.getLogger(TcpProtocol.class).info("处理消息: {}, 长度: {}", entry.getKey(), entry.getValue().length);
                            Logger.getLogger(TcpProtocol.class).info("队列: {}", getQueue.size());
                            synchronized (sendQueue) {

                                sendQueue.offer(Map.of(entry.getKey(), result)); // 加入消息队列
                                sendQueue.notifyAll(); // 通知发送线程有新的数据
                            }
                            synchronized (urgentQueue) {
                               if (PatternUtils.isListKeyWordOR(JsonResult, List.of("person"))){
                                   urgentQueue.offer(Map.of(entry.getKey(), JsonResult)); // 加入消息队列
                                   urgentQueue.notifyAll(); // 通知发送线程有新的数据
                               }

                            }
                            synchronized (messageQueue) {


                                if( count % 10 == 0){
                                    // 删除最后一个逗号
                                    if (!resultStr.isEmpty() && resultStr.charAt(resultStr.length()-1) == ',') {
                                        resultStr.deleteCharAt(resultStr.length()-1);
                                    }
                                    String resultStrStr = resultStr.toString();
                                    if (!resultStrStr.isEmpty()) {
                                        messageQueue.offer(Map.of(entry.getKey(),"{"+resultStrStr+"}")); // 加入消息队列
                                    }

                                    resultStr= new StringBuilder();
                                    messageQueue.notifyAll(); // 通知处理线程有新的数据
                                }
                            }

                            Logger.getLogger(TcpProtocol.class).info("发送队列长度: {}",sendQueue.size());
                            Logger.getLogger(TcpProtocol.class).info("处理消息耗时: {}ms", System.currentTimeMillis() - startTime);
                        }
                    }
                }
                if(!isConnected){
                    Logger.getLogger(TcpProtocol.class).info("客户端会话已断开，停止处理消息");
                    clientSocket.close();
                    break;
                }
            } catch (InterruptedException e) {
                Logger.getLogger(TcpProtocol.class).error("TCP协议处理异常",e);
                break;
            }
        }
        }finally {
            Logger.getLogger(TcpProtocol.class).info("客户端会话已断开，停止处理消息，isConnected:{}",isConnected);
        }

    }
    public void SendMqttMessage(Socket clientSocket) throws IOException {
        try{
            while (!clientSocket.isClosed() && isConnected) {
                long startTime = System.currentTimeMillis();
                try {
                    synchronized (urgentQueue) {
                        if (urgentQueue.isEmpty()) {
                            urgentQueue.wait(100); // 等待新消息，超时时间为500ms
                            continue;
                        }
                        Map<String, String> message = urgentQueue.poll(); // 取出消息
                        if (message != null) {
                            for (Map.Entry<String, String> entry : message.entrySet()) {
                                mqttService.sendToMqtt(entry.getValue(), entry.getKey()+"/urgent"); // 发送MQTT消息
                            }
                        }
                    }

                    synchronized (messageQueue) {
                        if (messageQueue.isEmpty()) {
                            messageQueue.wait(100); // 等待新消息，超时时间为500ms
                        }
                        Map<String, String> message = messageQueue.poll(); // 取出消息
                        if (message != null) {
                            for (Map.Entry<String, String> entry : message.entrySet()) {
                                if(count % 10 == 0){
                                    System.out.println("count:"+count);
                                    boolean result = redisZSetService.addDetectionResult_0(entry.getKey(), entry.getValue()); // 保存检测结果到Redis
                                    Logger.getLogger(TcpProtocol.class).info("操作redis结果:{}",result);
                                    mqttService.sendToMqtt(entry.getValue(), entry.getKey()); // 发送MQTT消息
                                }
                            }
                        }
                    }
                    long endTime = System.currentTimeMillis();
                    Logger.getLogger(TcpProtocol.class).info("发送MQTT消息耗时: {}ms", endTime - startTime);
                    if (!isConnected) {
                        Logger.getLogger(TcpProtocol.class).info("客户端会话已断开，停止发送MQTT消息");
                        clientSocket.close();
                        break;
                    }
                } catch (InterruptedException e) {
                    Logger.getLogger(TcpProtocol.class).error("TCP协议发送异常", e);
                }
            }
        }finally {
            Logger.getLogger(TcpProtocol.class).info("客户端会话已断开，停止发送MQTT消息，isConnected:{}",isConnected);

        }
    }

    public void SendMessage(Socket clientSocket, SimpMessagingTemplate template) throws IOException {
        try {

            while (!clientSocket.isClosed() && isConnected) {
                long startTime = System.currentTimeMillis();
                try {
                    synchronized (sendQueue) {
                        if (sendQueue.isEmpty()) {
                            sendQueue.wait(100); // 等待新消息，超时时间为500ms
                        }
                        Map<String, byte[]> message = sendQueue.poll(); // 取出消息
                        if (message != null) {
                            for (Map.Entry<String, byte[]> entry : message.entrySet()) {
                                String base64Image = Base64.getEncoder().encodeToString(entry.getValue());
                                template.convertAndSend(entry.getKey(), Collections.singletonMap("image", base64Image)); // 发送消息
                                Logger.getLogger(TcpProtocol.class).info("转发消息: {}, 长度: {}", entry.getKey(), entry.getValue().length);
                                Logger.getLogger(TcpProtocol.class).info("sendQueue队列: {}", sendQueue.size());
                            }
                        }

                    }
                    long endTime = System.currentTimeMillis();
                    Logger.getLogger(TcpProtocol.class).info("发送WebSocket消息耗时: {}ms", endTime - startTime);
                    if (!isConnected) {
                        Logger.getLogger(TcpProtocol.class).info("客户端会话已断开，停止发送消息");
                        clientSocket.close();
                        break;

                    }
                } catch (InterruptedException e) {
                    Logger.getLogger(TcpProtocol.class).error("TCP协议发送异常", e);
                }
            }
        }finally {
            Logger.getLogger(TcpProtocol.class).info("客户端会话已断开，停止发送消息，isConnected:{}",isConnected);
        }
    }

}
