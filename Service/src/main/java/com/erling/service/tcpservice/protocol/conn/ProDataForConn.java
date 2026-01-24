package com.erling.service.tcpservice.protocol.conn;

import com.erling.service.opencv.model.yolo.YoloV5;
import com.erling.utils.pattern.PatternUtils;
import lombok.Getter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ProDataForConn extends ObjConn<ProDataForConn> {

    @Getter
    public YoloV5  yoloV5;


    @Getter
    private List<String> deviceFollowList;


    public ProDataForConn setYoloV5(YoloV5 yoloV5){
        this.yoloV5=yoloV5;
        return this;
    }

    public void processImage(){
        try{
            int waitTime=0;
            while (!this.clientSocket.isClosed()){
                if(this.deviceFollowList==null){
                    synchronized (dataQueue.getDeviceFollowListQueue()) {  //等待队列
                        if(dataQueue.getDeviceFollowListQueue().isEmpty()){
                            dataQueue.getDeviceFollowListQueue().wait(100);
                            continue;
                        }
                        this.deviceFollowList = dataQueue.getDeviceFollowListQueue().poll(); // 从队列中获取关注设备列表
                        log.info("设置关注设备:{}",this.deviceFollowList);
                    }
                }
                if(waitTime>1000){
                    log.info("等待1000ms后,获取队列仍为空,关闭连接");
                    clientSocket.close();
                    break;
                }
                synchronized (dataQueue.getGetQueue()){
                    if (dataQueue.getGetQueue().isEmpty()){
                        dataQueue.getGetQueue().wait(100);
                        waitTime+=1;
                    }
                    else{
                        waitTime=0;
                        Map<String, byte[]> data = dataQueue.getGetQueue().poll();

                        for(Map.Entry<String, byte[]> entry : data.entrySet()){

//                            Map<String, byte[]> resultMap = yoloV5.DnnYoloV5Detection(
//                                    entry.getValue().length,entry.getValue()
//                            );
//                            Map<String, byte[]> resultMap = yoloV5.DnnYoloV5DebugSRTime(
//                                    entry.getValue().length,entry.getValue()
//                            );
                            long startTime = System.currentTimeMillis();
                            Map<String, byte[]> resultMap = yoloV5.Detection(
                                    entry.getValue().length,entry.getValue()
                            );
                            long endTime = System.currentTimeMillis();
                            log.info("处理时间:{}ms",endTime-startTime);
                            synchronized (dataQueue.getSendQueue()){   // 发送图像队列入队
                                dataQueue.getSendQueue().offer(
                                        Map.of(
                                                entry.getKey(),
                                                resultMap.values().iterator().next()
                                        )
                                );
                            }
                            if(PatternUtils.isListKeyWordOR(
                                    resultMap.keySet().iterator().next(),
                                     deviceFollowList
                                )
                            ){
                                synchronized (dataQueue.getUrgentQueue()){ // 紧急队列入队
                                    dataQueue.getUrgentQueue().offer(
                                            Map.of(
                                                    entry.getKey(),
                                                    resultMap
                                            )
                                    );
                                }
                            }
                            synchronized (dataQueue.getMessageQueue()){
                                dataQueue.getMessageQueue().offer(      // 发送消息队列入队
                                        Map.of(
                                                entry.getKey(),
                                                resultMap.keySet().iterator().next()
                                        )
                                );
                            }

                            log.info("处理完毕,结果为:{},预发送至:{}",
                                    resultMap.keySet().iterator().next(),
                                    data.keySet().iterator().next()
                            );
                        }
                    }
                }

            }
            if (clientSocket.isClosed()){
                yoloV5.Destroy();
                log.info("会话断开,已销毁YOLO实例");

            }
        }

        catch (Exception e){

            yoloV5.Destroy();
            try{
                clientSocket.close();
            }
            catch (IOException ex){
                log.error("关闭客户端socket时出错:{}",ex);
            }

            log.info("会话断开,已销毁YOLO实例,异常信息:{}",e);
            throw new RuntimeException(e);
        }

    }
}
