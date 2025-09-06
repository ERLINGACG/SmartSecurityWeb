package com.erling.service.tcpservice.ser;

import com.erling.lib.instance.Instance;
import com.erling.lib.instance.LibraryAnn;
import com.erling.lib.instance.Load;
import com.erling.lib.opencv.dnn.DnnDetector;
import com.erling.lib.opencv.struct.output.ImageData;
import com.erling.service.opencv.dnn.DnnDetectorServiceTest;
import com.erling.service.opencv.dnn.YoloDnnTest;
import com.erling.service.tcpservice.config.TcpConfig;
import com.erling.utils.log.Logger;
import com.sun.jna.Pointer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class TCPServiceTest_0 {
    private final SimpMessagingTemplate messagingTemplate;

    private final TcpConfig tcpConfig;
    private final ThreadPoolExecutor clientThreadPool_0 ; //连接线程池
    private final ThreadPoolExecutor clientThreadPool_1 ; //处理线程池
    private final ThreadPoolExecutor clientThreadPool_2 ; //发送线程池
    @Autowired
    private  DnnDetectorServiceTest dnnDetectorServiceTest;

    private final AtomicBoolean isRunning = new AtomicBoolean(true);

    private final Queue<Map<String, byte[]>> getQueue = new LinkedList<>();
    private final Queue<Map<String, byte[]>> sendQueue = new LinkedList<>();


    public TCPServiceTest_0(SimpMessagingTemplate messagingTemplate, TcpConfig tcpConfig) {
        this.messagingTemplate = messagingTemplate;
        this.tcpConfig = tcpConfig;


        this.clientThreadPool_0 = (ThreadPoolExecutor) Executors.
                newFixedThreadPool(
                        this.tcpConfig.
                                getConnectionThreads()
                );
        this.clientThreadPool_1 = (ThreadPoolExecutor) Executors.
                newFixedThreadPool(
                        this.tcpConfig.
                                getConnectionThreads()
                );
        this.clientThreadPool_2 = (ThreadPoolExecutor) Executors.
                newFixedThreadPool(
                        this.tcpConfig.
                                getConnectionThreads()
                );
        StartServer();
    }


    public void StartServer() {
            // 启动TCP服务端
            new Thread(()-> {
                try(ServerSocket serverSocket = new ServerSocket(25500)){
                    System.out.println("TCP服务启动，端口：" + 25500);
                    while (isRunning.get()) { // 添加循环监听
                        Socket clientSocket = serverSocket.accept();
                        handleLinked(clientSocket);
                    }
                }catch(Exception e){
                    Logger.getLogger(TCPServiceTest_0.class).error("启动TCP服务失败", e);
                }
            }).start();
    }
    private void handleLinked(Socket clientSocket) throws SocketException {
        System.out.println("TCP连接已建立：" + clientSocket.getInetAddress()+":"+clientSocket.getPort());
        clientSocket.setSoTimeout(tcpConfig.getConnectionTimeout()); // 设置10秒超时检测
        // 设置TCP无延迟，减少小数据包的延迟
        clientSocket.setTcpNoDelay(true);

        // 增加接收缓冲区大小
        clientSocket.setReceiveBufferSize(1024 * 1024); // 1MB
        clientThreadPool_0.submit(() -> {
            getData(clientSocket);
        });

        clientThreadPool_1.submit(this::processData);
        clientThreadPool_2.submit(this::sendMessages);


    }
    public void getData(Socket clientSocket) {
        try(DataInputStream input = new DataInputStream(clientSocket.getInputStream())){
            long lastActiveTime = System.currentTimeMillis();
            while (!clientSocket.isClosed()) {
                long start = System.currentTimeMillis();
                try{

                    byte[] headerData = new byte[40];
                    input.readFully(headerData); // 读取头部信息
                    lastActiveTime = System.currentTimeMillis(); // 重置活跃时间
                    ByteBuffer buffer = ByteBuffer.wrap(headerData).order(ByteOrder.LITTLE_ENDIAN);

                    byte[] topicData = new byte[32];
                    buffer.get(topicData);  // 读取32字节topic
                    int topicLength = buffer.getInt();  // 读取4字节topic长度
                    int bodyLength = buffer.getInt();   // 读取4字节消息体长度
                    // 验证topic长度有效性
                    if (topicLength < 0 || topicLength > 32) {
                        throw new IOException("无效的topic长度: " + topicLength);
                    }
                    String topic = new String(topicData, 0, topicLength, StandardCharsets.UTF_8);

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

                    byte[] binaryData = messageBuffer.toByteArray(); //二进制消息
                    synchronized (getQueue) {
                        getQueue.offer(Map.of(topic, binaryData)); // 加入消息队列
                        getQueue.notifyAll(); // 通知处理线程有新的数据
                    }
                    System.out.println("当前消息队列长度："+getQueue.size());
                }catch (SocketTimeoutException e){
                    if (System.currentTimeMillis() - lastActiveTime >= 10000) {
                        Logger.getLogger(TcpService.class).warn("10秒内无新数据，自动断开连接");
                        clientSocket.close();
                        break;
                    }
                }catch (Exception e){
                    Logger.getLogger(TCPServiceTest_0.class).error("获取数据失败", e);
                    break;
                }
                long end = System.currentTimeMillis();
                System.out.println("获取数据耗时："+(end-start));
            }
        }catch (Exception e){
            Logger.getLogger(TCPServiceTest_0.class).error("获取数据失败", e);
        }
    }
    public void processData() {
        YoloDnnTest yoloDnnTest=new YoloDnnTest();
        while (isRunning.get()) {
            try {
                Map<String, byte[]> data = null;
                synchronized (getQueue) {
                    while (getQueue.isEmpty() && isRunning.get()) {
                        getQueue.wait(1000); // 等待1秒或有新数据
                    }
                    if (!getQueue.isEmpty()) {
                        data = getQueue.poll();
                    }
                }

                if (data != null) {
                    // 这里可以添加数据处理逻辑
                    // 处理完成后，可以直接发送或放入另一个发送队列
//                    byte[] result = YoloDnnTest.TEST_D(data.values().iterator().next()
//                    ,data.values().iterator().next().length);
//                    byte[] result = dnnDetectorServiceTest.detectTest(data.values().iterator().next());
//
                    byte[] result = yoloDnnTest.TEST_D2(data.values().iterator().next());
                    synchronized (sendQueue) {
                        sendQueue.offer(Map.of(data.keySet().iterator().next(), result));
                        sendQueue.notifyAll(); // 通知发送线程有新的数据
                        System.out.println("处理完成：当前发送队列长度："+sendQueue.size());
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                Logger.getLogger(TCPServiceTest_0.class).error("处理数据失败", e);
            }
        }
    }

    public void sendMessages() {
        while (isRunning.get()) {
            try {
                Map<String, byte[]> data = null;
                synchronized (sendQueue) {
                    while (sendQueue.isEmpty() && isRunning.get()) {
                        sendQueue.wait(1000); // 等待1秒或有新数据
                    }
                    if (!sendQueue.isEmpty()) {
                        data = sendQueue.poll();
                    }
                }

                if (data != null) {
                    synchronized (sendQueue) {
                        sendMessage(data);
                        System.out.println("发送完成：当前发送队列长度："+sendQueue.size());
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                Logger.getLogger(TCPServiceTest_0.class).error("处理数据失败", e);
            }
        }
    }
    private void sendMessage(Map<String, byte[]> data) {
        long lastActiveTime = System.currentTimeMillis();
        if (data == null || data.isEmpty()) {
            return;
        }
        System.out.println("发送数据："+data.keySet().iterator().next());
        String base64Image = Base64.getEncoder().encodeToString(data.values().iterator().next());
        messagingTemplate.convertAndSend(data.keySet().iterator().next(), Collections.singletonMap("image", base64Image));
        long currentTime = System.currentTimeMillis();
        System.out.println("发送耗时："+(currentTime-lastActiveTime));

    }
}
