package com.erling.service.websocket.stomp;

import com.erling.service.opencv.dnn.DnnDetectorServiceTest;
import com.erling.service.opencv.dnn.YoloDnn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Base64;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class TCPServiceTest {
    private static final int TCP_PORT = 12347;
    private final SimpMessagingTemplate  messagingTemplate;

    private final ExecutorService clientThreadPool = Executors.newFixedThreadPool(10); // 根据需求调整线程数

    // 添加帧率统计变量
    private final AtomicInteger frameCount = new AtomicInteger(0);
    private final AtomicLong lastFpsTime = new AtomicLong(System.currentTimeMillis());
    private double currentFps = 0.0;
    @Autowired
    public TCPServiceTest(SimpMessagingTemplate messagingTemplate, DnnDetectorServiceTest dnnDetectorServiceTest) {
        this.messagingTemplate = messagingTemplate;
        startTCPServer();
        startFpsCalculator();
    }
    private void startFpsCalculator() {
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000); // 每秒计算一次FPS
                    long currentTime = System.currentTimeMillis();
                    long elapsedTime = currentTime - lastFpsTime.get();
                    int frames = frameCount.getAndSet(0);

                    if (elapsedTime > 0) {
                        currentFps = frames / (elapsedTime / 1000.0);
                    }

                    lastFpsTime.set(currentTime);
//                    System.out.printf("当前发送帧率: %.2f FPS%n", currentFps);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }).start();
    }

    protected void startTCPServer() {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(TCP_PORT)){
                System.out.println("TCP图片服务启动，端口：" + TCP_PORT);
                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    handleTcpClient_Pool(clientSocket);
                }
            } catch (Exception e) {
              System.err.println("TCP<UNK>: " + e.getMessage());
            }
        }).start();
    }

    private void handleTcpClient_Pool(Socket clientSocket) {
        clientThreadPool.submit(() -> {
            try (DataInputStream input = new DataInputStream(clientSocket.getInputStream())) {
                while (true) {
                    try {
                        byte[] header = new byte[40];
                        input.readFully(header);
                        ByteBuffer buffer = ByteBuffer.wrap(header).order(ByteOrder.LITTLE_ENDIAN);

                        byte[] topicBytes = new byte[32];
                        buffer.get(topicBytes);  // 读取32字节topic
                        int topicLength = buffer.getInt();  // 读取4字节topic长度
                        int bodyLength = buffer.getInt();   // 读取4字节消息体长度
                        // 验证topic长度有效性
                        if (topicLength < 0 || topicLength > 32) {
                            throw new IOException("无效的topic长度: " + topicLength);
                        }
                        String topic = new String(topicBytes, 0, topicLength, "UTF-8");

                        // 分块读取消息体（保持与TcpImageService相同的读取逻辑）
                        ByteArrayOutputStream messageBuffer = new ByteArrayOutputStream(bodyLength);
                        byte[] chunk = new byte[1024 * 10];
                        int remaining = bodyLength;

                        while (remaining > 0) {
                            int read = input.read(chunk, 0, Math.min(chunk.length, remaining));
                            if (read <= 0) throw new IOException("流意外结束");
                            messageBuffer.write(chunk, 0, read);
                            remaining -= read;
                        }

//                      String message = messageBuffer.toString(StandardCharsets.UTF_8);//文本消息
                        byte[] binaryData = messageBuffer.toByteArray(); //二进制消息
                         System.out.printf("收到数据: 头部长度=%d, 主题='%s', 消息体长度=%d%n",
                        header.length, topic, bodyLength);
// 发送前增加帧计数
                        frameCount.incrementAndGet();
                        byte[] imageData_ = YoloDnn.TEST_D(binaryData,binaryData.length);
                        // 在读取数据后添加日志


                        System.out.printf("[%s|%d] 收到图片数据，大小: %d bytes%n", topic, bodyLength, imageData_.length);
                        String base64Image = Base64.getEncoder().encodeToString(imageData_);
                        messagingTemplate.convertAndSend(topic, Collections.singletonMap("image", base64Image));
                    } catch (Exception e) {
                        System.out.println("客户端处理异常: " + e.getMessage());
                        break;
                    }
                }
            } catch (EOFException e) {
                System.out.println("连接正常关闭");
            } catch (Exception e) {
                System.out.println("客户端处理异常: " + e.getMessage());
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    System.err.println("关闭连接异常: " + e.getMessage());
                }
            }
        });
    }

    private boolean containsNewLine(byte[] data) {
        for (byte b : data) {
            if (b == '\n' || b == '\r') {
                return true;
            }
        }
        return false;
    }
}
