package com.erling.service.tcpservice.protocol.conn;

import com.erling.entity.detect.DetectionHistory;
import com.erling.service.detectHistroy.DetectionHistoryService;
import com.erling.service.mqtt.MqttService;
import com.erling.service.tcpservice.protocol.TcpProtocol;
import com.erling.utils.log.Logger;
import lombok.Getter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

import static com.erling.utils.fileU.FileUtils.saveFile;

public class SendMqttUrgForConn extends ObjConn<SendMqttUrgForConn>{

    @Getter
    private DetectionHistoryService detectionHistoryService;

    @Getter
    private MqttService  mqttService;

    public SendMqttUrgForConn setDetectionHistoryService(DetectionHistoryService detectionHistoryService) {
        this.detectionHistoryService = detectionHistoryService;
        return this;
    }

    public SendMqttUrgForConn setMqttService(MqttService mqttService) {
        this.mqttService = mqttService;
        return this;
    }

    public void saveFrame(String topic,String fileName, byte[] data,String path){
        try{
            saveFile(data,path,fileName);

            @SuppressWarnings("all")
            DetectionHistory detectionHistory = new DetectionHistory().
                    _setTopic(topic).
                    _setPath(path+"\\"+fileName).
                    _setDate(LocalDateTime.now());
            boolean isSuccess = detectionHistoryService.insert(detectionHistory);
            if(isSuccess){
               log.info("数据保存成功,topic:{}",topic);
            }else{
                log.info("数据保存失败,topic:{}",topic);
            }

        }catch (IOException e){
            Logger.getLogger(TcpProtocol.class).error("保存文件失败",e);
        }
    }
    public void SendMqttUrg(){
       try{
          while(!clientSocket.isClosed()){
              synchronized (dataQueue.getUrgentQueue()){
                  if(dataQueue.getUrgentQueue().isEmpty()){
//                      log.info("紧急队列为空，等待中");
                      dataQueue.getUrgentQueue().wait(100);
                  }
                  Map<String,Map<String,byte[]>> message = dataQueue.getUrgentQueue().poll(); // 取出消息
                  if (message != null) {
                      String topic = message.keySet().iterator().next();
                      Map<String,byte[]> data_res=message.values().iterator().next();

                      byte[] data =  data_res.values().iterator().next();
                      String resultMes=data_res.keySet().iterator().next();
                      saveFrame(
                            topic, System.currentTimeMillis()+".jpg",
                            data,   "E:\\SmartSecurity\\testPathOutput\\1"
                      );
                      mqttService.sendToMqtt(resultMes,topic+"/urgent");
                      log.info("已发送至紧急消息频道:{}",topic+"/urgent");
                  }
              }
          }
       }catch(Exception e){
           log.info("<UNK>Mqtt<UNK>:{}",e.getMessage());
       }
    }
}
