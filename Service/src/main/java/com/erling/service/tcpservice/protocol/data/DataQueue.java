package com.erling.service.tcpservice.protocol.data;

import com.erling.utils.log.Logger;
import lombok.Data;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

@Data
public class DataQueue {
    private final Queue<Map<String, byte[]>> getQueue = new LinkedList<>(); //

    private final Queue<Map<String, byte[]>> sendQueue = new LinkedList<>();

    private final Queue<Map<String, String>> messageQueue = new LinkedList<>();

    private final Queue<Map<String,Map<String,byte[]>>> urgentQueue = new LinkedList<>();

    public void clearQueue(){
        getQueue.clear();
        sendQueue.clear();
        messageQueue.clear();
        urgentQueue.clear();
        this.ShowQueueInfo();
    }



    public void ShowQueueInfo(){
        Logger.getLogger(this.getClass()).info("获取队列大小:{} | 发送队列大小:{} | 消息队列大小:{} | 紧急队列大小:{}",getQueue.size(),sendQueue.size(),messageQueue.size(),urgentQueue.size());

    }
}
