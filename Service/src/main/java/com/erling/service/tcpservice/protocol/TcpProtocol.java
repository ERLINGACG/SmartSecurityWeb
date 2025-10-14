package com.erling.service.tcpservice.protocol;

import com.erling.entity.detect.DetectionHistory;
import com.erling.service.detectHistroy.DetectionHistoryService;
import com.erling.service.mqtt.MqttService;
import com.erling.service.opencv.dnn.YoloDnn;
import com.erling.service.redis.ser.RedisZSetService;
import com.erling.utils.log.Logger;
import com.erling.utils.pattern.PatternUtils;
import lombok.Getter;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.*;

public class TcpProtocol {
    private final Queue<Map<String, byte[]>> getQueue = new LinkedList<>();

    private final Queue<Map<String, byte[]>> sendQueue = new LinkedList<>();

    private final Queue<Map<String, String>> messageQueue = new LinkedList<>();

    private final Queue<Map<String,Map<String,byte[]>>> urgentQueue = new LinkedList<>();

    private final MqttService mqttService;

    private final RedisZSetService  redisZSetService;
    private final YoloDnn yoloDnn;

    private final DetectionHistoryService  detectionHistoryService;

    @Getter
    private volatile boolean isConnected = true;


    int count = 0;
    public TcpProtocol(
            YoloDnn yoloDnn,
            RedisZSetService redisZSetService,
            MqttService mqttService,
            DetectionHistoryService detectionHistoryService) {
        this.mqttService = mqttService;
        this.redisZSetService = redisZSetService;
        this.yoloDnn = yoloDnn;
        this.detectionHistoryService = detectionHistoryService;
    }

    public void saveFile(String topic,String fileName, byte[] data,String path) {
         try{
             Path dirPath = Paths.get(path);
             if(!Files.exists(dirPath)){
                 Files.createDirectories(dirPath);
             }
             Path filePath = dirPath.resolve(fileName);
             Files.write(filePath, data, StandardOpenOption.CREATE_NEW); // 写入文件
             Logger.getLogger(TcpProtocol.class).info("保存文件成功，文件名：{}，文件路径：{}",fileName,path);
             DetectionHistory detectionHistory = new DetectionHistory();
             detectionHistory.setTopic(topic);
             detectionHistory.setPath(path+"\\"+fileName);
             detectionHistory.setDate(LocalDateTime.now());
             boolean isSuccess = detectionHistoryService.insert(detectionHistory);
             if(isSuccess){
                 Logger.getLogger(TcpProtocol.class).info("数据保存成功");
             }else{
                 Logger.getLogger(TcpProtocol.class).info("数据保存失败");
             }

         }catch (IOException e){
             Logger.getLogger(TcpProtocol.class).error("保存文件失败",e);
         }
    }

    public void resetAllQueues() {
        synchronized (getQueue) {
            getQueue.clear();
        }
        synchronized (urgentQueue) {
            urgentQueue.clear();
        }
        synchronized (sendQueue) {
            sendQueue.clear();
        }
    }
    public  void processIO(Socket clientSocket) throws IOException {
        try(DataInputStream input = new DataInputStream(clientSocket.getInputStream())){
            ProcessingStandards<?> processingStandards = new ProcessingStandards<>();
            long lastActiveTime = System.currentTimeMillis();
            if(clientSocket.isConnected()){
                processingStandards.readData(input);
                String topic = processingStandards.getTopic();
                String key = (String) processingStandards.getBody(ProcessingStandards.ReadMode.TEXT_MODE);
                Logger.getLogger(TcpProtocol.class).info("topic:{},key:{}",topic,key);
                if(!key.equals("keyTest")){ //可更换密钥
                    clientSocket.close();
                }
            }
            while (!clientSocket.isClosed()) {
                long startTime = System.currentTimeMillis();
                Logger.getLogger(TcpProtocol.class).info("当前客户端会话:{}",clientSocket.getInetAddress());
                try{
                     processingStandards.readData(input);
                     String topic = processingStandards.getTopic();
                     byte[] bodyData =(byte[]) processingStandards.getBody(ProcessingStandards.ReadMode.BINARY_MODE);
                        synchronized (getQueue) {
                            getQueue.offer(Map.of(topic, bodyData)); // 加入消息队列
                            getQueue.notifyAll(); // 通知处理线程有新的数据
                            long endTime = System.currentTimeMillis();
                            Logger.getLogger(TcpProtocol.class).debug("收到消息: {}, 长度: {}", topic, bodyData.length);
                            Logger.getLogger(TcpProtocol.class).debug("队列长度: {}", getQueue.size());
                            Logger.getLogger(TcpProtocol.class).debug("收到消息耗时: {}ms", endTime - startTime);
                        }

                }catch (SocketTimeoutException e){
                    if (System.currentTimeMillis() - lastActiveTime >= 30000) {
                        Logger.getLogger(TcpProtocol.class).debug("未收到数据30秒，即将断开连接");
                        clientSocket.close();
                        isConnected = false;
                        resetAllQueues();
                        break;
                    }
                }catch (IOException e){
                    clientSocket.close();
                    isConnected = false;
                    Logger.getLogger(TcpProtocol.class).error("TCP协议处理异常：{}，断开连接：{}",e,isConnected);
                    resetAllQueues();
                    break;
                }
            }
        }catch(EOFException e){
            Logger.getLogger(TcpProtocol.class).debug("客户端主动断开连接",e);
            isConnected = false;
            resetAllQueues();
        }finally {
            Logger.getLogger(TcpProtocol.class).debug("客户端会话已断开isConnected:{}",isConnected);
            resetAllQueues();
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
                            Map<String, byte[]> resultMap = yoloDnn.TEST_D3(entry.getValue());

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
                                   urgentQueue.offer(Map.of(entry.getKey(), Map.of(JsonResult,result))); // 加入消息队列
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
                        Map<String, Map<String, byte[]>> message = urgentQueue.poll(); // 取出消息
                        if (message != null) {
                            for (Map.Entry<String, Map<String, byte[]>> entry : message.entrySet()) {
                                Map<String, byte[]> value = entry.getValue();
                                String JsonResult = value.keySet().iterator().next();
                                byte[] result = value.values().iterator().next();

                                if (count % 10 == 0){ // 每10帧保存一次关键图片
                                    saveFile(entry.getKey(),System.currentTimeMillis()+".jpg",
                                            result,
                                            "E:\\SmartSecurity\\testPathOutput\\1");
                                }
                                mqttService.sendToMqtt(JsonResult, entry.getKey()+"/urgent"); // 发送MQTT消息
                                Logger.getLogger(TcpProtocol.class).info("发送MQTT消息: {}", entry.getKey());
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
                                template.convertAndSend(entry.getKey()+"/image", Collections.singletonMap("image", base64Image)); // 发送消息
                                Logger.getLogger(TcpProtocol.class).info("转发消息: {}, 长度: {}", entry.getKey()+"/image", entry.getValue().length);
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
