package com.erling.service.tcpservice.protocol.conn;

import com.erling.service.tcpservice.protocol.data.DataQueue;
import com.erling.utils.log.Logger;
import lombok.Getter;

import java.net.Socket;

public class ObjConn<T extends ObjConn<T>> {


    protected final org.slf4j.Logger logger =  Logger.getLogger(this.getClass());

    protected Logger.log log ;

    @Getter
    protected Socket clientSocket;

     @Getter
     protected DataQueue dataQueue;


    @SuppressWarnings("unchecked")
    public T setClientSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T setDataQueue(DataQueue dataQueue) {
        this.dataQueue = dataQueue;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T setLog(boolean isDebug) {
        this.log = Logger.createLog().
                          setLogger(logger).
                          setLogIsEnable(isDebug);
        return (T) this;
    }
}
