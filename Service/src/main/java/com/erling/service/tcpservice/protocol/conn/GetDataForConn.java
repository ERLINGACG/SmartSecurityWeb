package com.erling.service.tcpservice.protocol.conn;

import com.erling.service.redis.ser.RedisDeviceConfig;
import com.erling.service.redis.ser.RedisDeviceInfoService;
import com.erling.service.tcpservice.protocol.ProcessingStandards;
import lombok.Getter;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Map;



public class GetDataForConn extends ObjConn<GetDataForConn>  {


    @Getter
    private String connKey;

    @Getter
    private Long timeOut;

    @Getter
    private String subscribeTopic;


    @Getter
    private RedisDeviceInfoService redisDeviceInfoService;

    @Getter
    private RedisDeviceConfig redisDeviceConfig;

    @Getter
    private List<String> deviceFollowList;

    public GetDataForConn setRedisDeviceConfig(RedisDeviceConfig redisDeviceConfig) {
        this.redisDeviceConfig = redisDeviceConfig;
        return this;
    }

    public GetDataForConn setRedisDeviceInfoService(RedisDeviceInfoService redisDeviceInfoService) {
        this.redisDeviceInfoService = redisDeviceInfoService;
        return this;
    }

    public GetDataForConn setConnKey(String connKey) {
        this.connKey = connKey;
        log.info("设置连接密钥");
        return this;
    }

     public GetDataForConn setTimeout(int timeOut) {
        this.timeOut = (long) timeOut;
        log.info("设置连接超时时间:{}ms",timeOut);
        return this;
    }


    public void Close(){ // 关闭连接
        try{
            clientSocket.close();
            dataQueue.clearQueue();
            redisDeviceInfoService.setDeviceStatus(subscribeTopic, false);
            log.info("会话:{}:{} 关闭连接成功,设置连接topic:{}状态为OFFLINE",
                    clientSocket.getInetAddress().getHostAddress(),
                    clientSocket.getPort(),
                    subscribeTopic
            );

        }
        catch (IOException e){
            log.error("会话:{}:{} 关闭连接异常",
                        clientSocket.getInetAddress().getHostAddress(),
                        clientSocket.getPort()
            );
        }
    }
    public void Connect(){
        try(DataInputStream input = new DataInputStream(
                clientSocket.getInputStream()
            )
        ){
            log.info("当前会话:{}:{}",
                    clientSocket.getInetAddress().getHostAddress(),
                    clientSocket.getPort()
            );
            long lastActiveTime = System.currentTimeMillis();
            ProcessingStandards<?> processingStandards = new ProcessingStandards<>();
            if(this.clientSocket.isConnected()) {
                processingStandards.readData(input);
                String topic = processingStandards.getTopic();                                             //从处理标准中获取topic
                String key = (String) processingStandards.getBody(ProcessingStandards.ReadMode.TEXT_MODE); // 从处理标准中获取密钥
                log.info("连接topic:{},密钥:{}", topic, key);

                if (!key.equals(this.connKey)) { //可更换密钥
                    Close();      // 关闭连接
                    log.error("密钥校验错误,当前设置密钥:{},目标设备密钥:{}", this.connKey, key);
                    return;
                }
                this.subscribeTopic = topic;
                redisDeviceInfoService.setDeviceStatus(subscribeTopic, true);

                deviceFollowList = redisDeviceConfig.getDeviceFollow(topic);
                dataQueue.getDeviceFollowListQueue().offer(deviceFollowList);

                log.info("连接topic:{} 关注设备:{}", topic, deviceFollowList);

                lastActiveTime = System.currentTimeMillis();
            }
            while(true){
                try{
                    processingStandards.readData(input);
                    String topic = processingStandards.getTopic();
                    byte[] bodyData =(byte[]) processingStandards.getBody(ProcessingStandards.ReadMode.BINARY_MODE);
                    synchronized (dataQueue.getGetQueue()) {
                        dataQueue.getGetQueue().offer(Map.of(topic, bodyData)); // 加入消息队列
                        dataQueue.getGetQueue().notifyAll();                    // 通知处理线程有新的数据
                        log.info("收到消息：{}, 数据长度: {},[获取队列]长度：{}",
                                topic,
                                bodyData.length,
                                dataQueue.getGetQueue().size()
                        );
                        lastActiveTime = System.currentTimeMillis();
                    }
                    log.info("当前会话:{}:{} 链接状态:{}",
                            clientSocket.getInetAddress().getHostAddress(),
                            clientSocket.getPort(),
                            !clientSocket.isClosed()
                    );
                }
                catch (SocketTimeoutException e){
                    if(System.currentTimeMillis() - lastActiveTime >= timeOut){
                        log.info("会话:{}:{} 超时,未收到数据",
                                clientSocket.getInetAddress(),
                                clientSocket.getPort()
                        );
                        Close();
                        break;
                    }
                }

                catch(EOFException e){
                    log.info("会话:{}:{} 主动关闭连接",
                            clientSocket.getInetAddress(),
                            clientSocket.getPort()
                    );
                    Close();
                    break;
                }
                if(clientSocket.isClosed()){
                    Close();
                }

            }

        }
        catch (IOException e) {
            log.error("会话:{}:{} 连接异常,关闭连接",
                            clientSocket.getInetAddress(),
                            clientSocket.getPort()
            );
            Close();
            throw new RuntimeException(e);
        }

    }
}
