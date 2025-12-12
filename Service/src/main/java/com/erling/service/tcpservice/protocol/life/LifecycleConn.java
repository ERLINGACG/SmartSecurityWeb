package com.erling.service.tcpservice.protocol.life;

import com.erling.service.tcpservice.protocol.conn.*;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class LifecycleConn extends LifecycleDataObj<LifecycleConn>{


    public LifecycleConn initConn() {

        GetDataForConn getDataForConn = new GetDataForConn().
                setLog(enable).
                setClientSocket(clientSocket).
                setConnKey(this.connKey).
                setDataQueue(dataQueue).
                setTimeout(Math.toIntExact(timeOut)).setRedisDeviceInfoService(redisDeviceInfoService);
        ConnItem.add(getDataForConn);
        return this;
    }
    public LifecycleConn initProc() {
        ProDataForConn proDataForConn = new ProDataForConn().
                setLog(enable).
                setClientSocket(clientSocket).
                setDataQueue(dataQueue).
//                setDnn(yoloDnn).
                setYoloV5(yoloV5);
        ConnItem.add(proDataForConn);
        return this;
    }

    public LifecycleConn initSendImage() {
        SendWebSForConn sendWebSForConn = new SendWebSForConn().
                setLog(enable).
                setClientSocket(clientSocket).
                setDataQueue(dataQueue).
                setTemplate(template);
        ConnItem.add(sendWebSForConn);
        return this;
    }

    public LifecycleConn initSendMqtt() {
        SendMqttMegForConn sendMqttForConn = new SendMqttMegForConn().
                setLog(enable).
                setClientSocket(clientSocket).
                setDataQueue(dataQueue).
                setRedisZSetService(redisZSetService).
                setMqttService(mqttService);
        ConnItem.add(sendMqttForConn);
        return this;
    }

   public LifecycleConn initSendUrgMqtt() {
       SendMqttUrgForConn sendMqttUrgForConn = new SendMqttUrgForConn().
                       setLog(enable).
                       setClientSocket(clientSocket).
                       setDataQueue(dataQueue).
                       setDetectionHistoryService(detectionHistoryService).
                       setMqttService(mqttService);
        ConnItem.add(sendMqttUrgForConn);
        return this;
    }



    public void SubmitPool(){
        clientThreadPool.submit( ((GetDataForConn)      ConnItem.get(0))::Connect);
        clientThreadPool.submit( ((ProDataForConn)      ConnItem.get(1))::processImage);
        clientThreadPool.submit( ((SendWebSForConn)     ConnItem.get(2))::SendByteAndTop);
        clientThreadPool.submit( ((SendMqttMegForConn)  ConnItem.get(3))::SendMqttMeg);
        clientThreadPool.submit( ((SendMqttUrgForConn)  ConnItem.get(4))::SendMqttUrg);
    }

    public static void run(LifecycleConn conn){
        conn.SubmitPool();
    }


    public LifecycleConn() {

    }


}
