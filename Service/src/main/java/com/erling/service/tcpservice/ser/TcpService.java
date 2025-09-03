package com.erling.service.tcpservice.ser;

import com.erling.service.opencv.dnn.DnnDetectorServiceTest;
import com.erling.service.tcpservice.config.TcpConfig;
import com.erling.utils.log.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;


@Component
public class TcpService {
    private final SimpMessagingTemplate messagingTemplate;

    private final DnnDetectorServiceTest dnnDetectorServiceTest;
    private final TcpConfig tcpConfig;
    private final ThreadPoolExecutor clientThreadPool ; // 根据需求调整线程数

    @Autowired
    public TcpService(SimpMessagingTemplate messagingTemplate, DnnDetectorServiceTest dnnDetectorServiceTest,
                      TcpConfig tcpConfig) {
        this.messagingTemplate = messagingTemplate;
        this.dnnDetectorServiceTest = dnnDetectorServiceTest;
        this.tcpConfig = tcpConfig;
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
                       handleSocket(clientSocket);
                   }
               }catch(Exception e){
                   Logger.getLogger(TcpService.class).error("启动TCP服务失败", e);
               }
           }).start();
    }
    private void handleSocket(Socket clientSocket) throws SocketException {
        System.out.println("TCP连接已建立：" + clientSocket.getInetAddress()+":"+clientSocket.getPort());
        System.out.println("最大线程数: " + clientThreadPool.getMaximumPoolSize());
        clientSocket.setSoTimeout(tcpConfig.getConnectionTimeout()); // 设置10秒超时检测
        clientThreadPool.submit(() -> {
            System.out.println("当前活跃线程数: " + clientThreadPool.getActiveCount());
            System.out.println("线程池大小: " + clientThreadPool.getPoolSize());
             try(DataInputStream input = new DataInputStream(clientSocket.getInputStream())){
                 long lastActiveTime = System.currentTimeMillis();
                 while (!clientSocket.isClosed()) {
                    try {
                        System.out.println("[处理中] 活跃线程: " + clientThreadPool.getActiveCount());
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
                        byte[] chunk = new byte[1024 * 10];
                        int remaining = bodyLength;

                        while (remaining > 0) { // 循环读取消息体
                            int read = input.read(chunk, 0, Math.min(chunk.length, remaining));
                            if (read <= 0) throw new IOException("流意外结束");
                            lastActiveTime = System.currentTimeMillis(); // 重置活跃时间
                            messageBuffer.write(chunk, 0, read);
                            remaining -= read;
                        }

//                      String message = messageBuffer.toString(StandardCharsets.UTF_8);//文本消息
                        byte[] binaryData = messageBuffer.toByteArray(); //二进制消息
                        try{
                            byte[] data =  dnnDetectorServiceTest.detectTest(binaryData); // 调用模型预测
                            System.out.printf("[%s|%d] 收到图片数据，大小: %d bytes%n", topic, bodyLength, binaryData.length);
                            String base64Image = Base64.getEncoder().encodeToString(data);
                            messagingTemplate.convertAndSend(topic, Collections.singletonMap("image", base64Image));
                        }catch(Exception e){
                            Logger.getLogger(TcpService.class).error("解析TCP消息失败", e);
                        }



                    }catch (SocketTimeoutException e){
                        if (System.currentTimeMillis() - lastActiveTime >= 10000) {
                            Logger.getLogger(TcpService.class).warn("10秒内无新数据，自动断开连接");
                            break;
                        }
                    }
                 }
             }
             catch (EOFException e) {
                 Logger.getLogger(TcpService.class).info("正常关闭TCP连接");
             } catch(Exception e){
                 Logger.getLogger(TcpService.class).error("线程池处理TCP连接失败", e);
             }
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

        });
    }

}
