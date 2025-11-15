package com.erling.service.tcpservice.ser;

import com.erling.service.config.DebugConfig;
import com.erling.service.detectHistroy.DetectionHistoryService;
import com.erling.service.mqtt.MqttService;
import com.erling.service.opencv.dnn.YoloDnn;
import com.erling.service.opencv.dnn.model.YoloV5;
import com.erling.service.redis.ser.RedisZSetService;
import com.erling.service.tcpservice.config.TcpConfig;
import com.erling.service.tcpservice.protocol.TcpProtocol;
import com.erling.service.tcpservice.protocol.data.DataQueue;
import com.erling.service.tcpservice.protocol.life.LifecycleConn;
import com.erling.utils.log.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;


@Component
public class TcpService {
    private final SimpMessagingTemplate messagingTemplate;

    private final TcpConfig tcpConfig;

    private final DebugConfig  debugConfig;
    private final ThreadPoolExecutor clientThreadPool ; // 根据需求调整线程数


    private final RedisZSetService redisZSetService;

    private final MqttService mqttService;

    private final DetectionHistoryService  detectionHistoryService;

    @Autowired
    public TcpService(SimpMessagingTemplate messagingTemplate,
                      TcpConfig tcpConfig,
                      RedisZSetService redisZSetService,
                      MqttService mqttService,
                      DetectionHistoryService detectionHistoryService,
                      DebugConfig debugConfig) {
        this.messagingTemplate = messagingTemplate;
        this.tcpConfig = tcpConfig;
        this.debugConfig = debugConfig;
        this.mqttService = mqttService;
        this.redisZSetService = redisZSetService;
        this.detectionHistoryService = detectionHistoryService;
        this.clientThreadPool = (ThreadPoolExecutor)Executors.
                newFixedThreadPool(
                        this.tcpConfig.
                                getConnectionThreads()
                );
        StartTcpServer();
    }
    protected void StartTcpServer(){
           new Thread(()-> {
               try(ServerSocket serverSocket = new ServerSocket(tcpConfig.getPort())){
                   System.out.println("TCP服务启动，端口：" + tcpConfig.getPort());
                   while (true) {
                       Socket clientSocket = serverSocket.accept();
                       System.out.println("TCP<UNK>" + clientSocket.getInetAddress().getHostAddress());
                       LifeConn(clientSocket);
                   }
               }catch(Exception e){
                   Logger.getLogger(TcpService.class).error("启动TCP服务失败", e);
               }
           }).start();
    }

    private void handleSocket_2(Socket clientSocket) throws SocketException {
        System.out.println("TCP连接已建立：" + clientSocket.getInetAddress()+":"+clientSocket.getPort());
        clientSocket.setSoTimeout(tcpConfig.getConnectionTimeout()); // 设置10秒超时检测
        TcpProtocol protocol = new TcpProtocol(new YoloDnn(),redisZSetService,mqttService,detectionHistoryService);

        clientThreadPool.submit(() ->{
            try {
                protocol.processIO(clientSocket);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        clientThreadPool.submit(() ->{
            try {
                protocol.ProcessMessage(clientSocket);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        clientThreadPool.submit(()->{
            try {
                protocol.SendMqttMessage(clientSocket);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        clientThreadPool.submit(() ->{
            try {
                protocol.SendMessage(clientSocket,messagingTemplate);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void LifeConn(Socket clientSocket){
        DataQueue dataQueue = new DataQueue();
        LifecycleConn.run(new LifecycleConn().
                setLogEnable(debugConfig.isLog()).                  //启用日志
                setConnKey(tcpConfig.getConnKey()).                 //设置连接密钥
                setTimeout(tcpConfig.getConnectionTimeout()).       //设置连接超时时间
                setClientThreadPool(clientThreadPool).              //绑定线程池
                setTemplate(messagingTemplate).                     //绑定WebSocket消息模板
                setMqttService(mqttService).                        //绑定MQTT服务
                setRedisZSetService(redisZSetService).              //绑定Redis服务
                setDetectionHistoryService(detectionHistoryService).//绑定检测历史服务
                setClientSocket(clientSocket).                      //绑定客户端
                setDataQueue(dataQueue).                            //绑定数据队列
                setYoloV5(new YoloV5()).
                initConn().
                initProc().
                initSendImage().
                initSendMqtt().
                initSendUrgMqtt()
        );

    }
//    public void handleSocket(Socket clientSocket) throws SocketException {
//        DataQueue dataQueue = new DataQueue();
//
//        GetDataForConn getDataForConn =
//                new GetDataForConn().
//                        setLog(debugConfig.isLog()).
//                        setConnKey(tcpConfig.getConnKey()).
//                        setTimeout(tcpConfig.getConnectionTimeout()).
//                        setClientSocket(clientSocket).
//                        setDataQueue(dataQueue);
//
//        ProDataForConn proDataForConn =
//                new ProDataForConn().
//                        setLog(debugConfig.isLog()).
//                        setClientSocket(clientSocket).
//                        setDataQueue(dataQueue).
//                        setDnn(new YoloDnn());
//        SendWebSForConn sendWebSForConn =
//                new SendWebSForConn().
//                        setLog(debugConfig.isLog()).
//                        setClientSocket(clientSocket).
//                        setDataQueue(dataQueue).
//                        setTemplate(messagingTemplate);
//
//        SendMqttMegForConn sendMqttMegForConn =
//                new SendMqttMegForConn().
//                        setLog(debugConfig.isLog()).
//                        setClientSocket(clientSocket).
//                        setDataQueue(dataQueue).
//                        setRedisZSetService(redisZSetService).
//                        setMqttService(mqttService);
//
//        SendMqttUrgForConn sendMqttUrgForConn =
//                new SendMqttUrgForConn().
//                        setLog(debugConfig.isLog()).
//                        setClientSocket(clientSocket).
//                        setDataQueue(dataQueue).
//                        setDetectionHistoryService(detectionHistoryService).
//                        setMqttService(mqttService);
//
//
//
//        clientThreadPool.submit(getDataForConn::Connect);
//        clientThreadPool.submit(proDataForConn::processImage);
//        clientThreadPool.submit(sendWebSForConn::SendByteAndTop);
//        clientThreadPool.submit(sendMqttMegForConn::SendMqttMeg);
//        clientThreadPool.submit(sendMqttUrgForConn::SendMqttUrg);
//
//
//
//    }
//    @Deprecated
//    private void handleSocket_1(Socket clientSocket) throws SocketException {
//        System.out.println("TCP连接已建立：" + clientSocket.getInetAddress()+":"+clientSocket.getPort());
//        clientSocket.setSoTimeout(tcpConfig.getConnectionTimeout()); // 设置10秒超时检测
//        clientThreadPool.submit(() -> {
//            TcpProtocol protocol = new TcpProtocol(new YoloDnn(),redisZSetService,mqttService);
//           clientThreadPool.submit(() -> {
//               try {
//                   protocol.processIO(clientSocket);
//               } catch (IOException e) {
//                   throw new RuntimeException(e);
//               }
//           });
//           clientThreadPool.submit(() -> {
//               try {
//                   protocol.ProcessMessage(clientSocket);
//               } catch (IOException e) {
//                   throw new RuntimeException(e);
//               }
//           });
//           clientThreadPool.submit(()->{
//               try {
//                   protocol.SendMqttMessage(clientSocket);
//               } catch (IOException e) {
//                   throw new RuntimeException(e);
//               }
//           });
//           clientThreadPool.submit(() -> {
//               try {
//                   protocol.SendMessage(clientSocket,messagingTemplate);
//               } catch (IOException e) {
//                   throw new RuntimeException(e);
//               }
//           });
//        });
//    }
//   @Deprecated
//    private void handleSocket_0(Socket clientSocket) throws SocketException {
//        System.out.println("TCP连接已建立：" + clientSocket.getInetAddress()+":"+clientSocket.getPort());
//        clientThreadPool.submit(() ->{
//            TcpProtocol tcpProtocol = new TcpProtocol(new YoloDnn(),redisZSetService,mqttService);
//            new Thread(()->{
//                try {
//                    tcpProtocol.processIO(clientSocket);
//                } catch (IOException e) {
//                    Logger.getLogger(TcpService.class).error("TCP协议处理异常", e);
//                    throw new RuntimeException(e);
//                }
//            }).start();
//            new Thread(()->{
//                try {
//                    tcpProtocol.ProcessMessage(clientSocket);
//                } catch (IOException e) {
//                    Logger.getLogger(TcpService.class).error("TCP协议处理异常", e);
//                    throw new RuntimeException(e);
//                }
//            }).start();
//            new Thread(()->{
//                try {
//                    tcpProtocol.SendMessage(clientSocket,messagingTemplate);
//                } catch (IOException e) {
//                    Logger.getLogger(TcpService.class).error("TCP协议发送异常",e);
//                    throw new RuntimeException(e);
//                }
//            }).start();
//            Logger.getLogger(TcpService.class).info("get队列长度：{}", tcpProtocol.getQueueSize());
//            Logger.getLogger(TcpService.class).info("send队列长度：{}", tcpProtocol.getSendQueueSize());
//        });
//
//    }

//    private void handleSocket(Socket clientSocket) throws SocketException {
//        System.out.println("TCP连接已建立：" + clientSocket.getInetAddress()+":"+clientSocket.getPort());
//        System.out.println("最大线程数: " + clientThreadPool.getMaximumPoolSize());
//        clientSocket.setSoTimeout(tcpConfig.getConnectionTimeout()); // 设置10秒超时检测
//        clientThreadPool.submit(() -> {
//            YoloDnn yoloDnnTest=new YoloDnn();
//            System.out.println("当前活跃线程数: " + clientThreadPool.getActiveCount());
//            System.out.println("线程池大小: " + clientThreadPool.getPoolSize());
//             try(DataInputStream input = new DataInputStream(clientSocket.getInputStream())){
//                 long lastActiveTime = System.currentTimeMillis();
//                 while (!clientSocket.isClosed()) {
//                    try {
//                        System.out.println("[处理中] 活跃线程: " + clientThreadPool.getActiveCount());
//                        byte[] header = new byte[40];
//                        input.readFully(header);
//                        lastActiveTime = System.currentTimeMillis(); // 重置活跃时间
//                        ByteBuffer buffer = ByteBuffer.wrap(header).order(ByteOrder.LITTLE_ENDIAN);
//
//                        byte[] topicBytes = new byte[32];
//                        buffer.get(topicBytes);  // 读取32字节topic
//                        int topicLength = buffer.getInt();  // 读取4字节topic长度
//                        int bodyLength = buffer.getInt();   // 读取4字节消息体长度
//                        // 验证topic长度有效性
//                        if (topicLength < 0 || topicLength > 32) {
//                            throw new IOException("无效的topic长度: " + topicLength);
//                        }
//                        String topic = new String(topicBytes, 0, topicLength, StandardCharsets.UTF_8);
//
//                        // 分块读取消息体（保持与TcpImageService相同的读取逻辑）
//                        ByteArrayOutputStream messageBuffer = new ByteArrayOutputStream(bodyLength);
//                        byte[] bodyData = new byte[bodyLength];
//                        input.readFully(bodyData);
//                        lastActiveTime = System.currentTimeMillis(); // 重置活跃时间

// ... existing code ...
                        //二进制消息
                        //                        byte[] chunk = new byte[1024 * 10];
//                        int remaining = bodyLength;

//                        while (remaining > 0) { // 循环读取消息体
//                            int read = input.read(chunk, 0, Math.min(chunk.length, remaining));
//                            if (read <= 0) throw new IOException("流意外结束");
//                            lastActiveTime = System.currentTimeMillis(); // 重置活跃时间
//                            messageBuffer.write(chunk, 0, read);
//                            remaining -= read;
//                        }

//                      String message = messageBuffer.toString(StandardCharsets.UTF_8);//文本消息
//                        byte[] binaryData = messageBuffer.toByteArray(); //二进制消息
//                        try{
//                            byte[] data = yoloDnnTest.TEST_D2(bodyData);
//                            System.out.printf("[%s|%d] 收到图片数据，大小: %d bytes%n", topic, bodyLength, bodyData.length);
//                            String base64Image = Base64.getEncoder().encodeToString(data);
//                            messagingTemplate.convertAndSend(topic, Collections.singletonMap("image", base64Image));
//                        }catch(Exception e){
//                            Logger.getLogger(TcpService.class).error("解析TCP消息失败", e);
//                        }
//
//
//
//                    }catch (SocketTimeoutException e){
//                        if (System.currentTimeMillis() - lastActiveTime >= 10000) {
//                            Logger.getLogger(TcpService.class).warn("10秒内无新数据，自动断开连接");
//                            break;
//                        }
//                    }
//                 }
//             }
//             catch (EOFException e) {
//                 Logger.getLogger(TcpService.class).info("正常关闭TCP连接");
//             } catch(Exception e){
//                 Logger.getLogger(TcpService.class).error("线程池处理TCP连接失败", e);
//             }
//             }finally {
//                 try {
//                     clientSocket.close();
//                     System.out.println("TCP连接已关闭：" + clientSocket.getInetAddress()+":"+clientSocket.getPort());
//
//                 }catch (Exception e){
//                     Logger.getLogger(TcpService.class).error("TCP连接关闭失败", e);
//                 }
//                 System.out.println("[任务结束] 活跃线程: " + clientThreadPool.getActiveCount());
//             }

//        });
//    }

}
