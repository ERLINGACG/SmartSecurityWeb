package com.erling.service.tcpservice.protocol;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProcessingStandards<T> { // 处理标准

    private byte[] headerBytes = new byte[40];
    private  String topic;
    private  T body;

    public void readData(DataInputStream input) throws IOException {
        input.readFully(headerBytes);
        ByteBuffer buffer = ByteBuffer.wrap(headerBytes).order(ByteOrder.LITTLE_ENDIAN);

        byte[] topicBytes = new byte[32];
        buffer.get(topicBytes);  // 读取32字节topic

        int topicLength = buffer.getInt();  // 读取4字节topic长度
        int BodyLength = buffer.getInt();   // 读取4字节消息体长度

        topic = new String(topicBytes, 0, topicLength, StandardCharsets.UTF_8);

        byte[] bodyBytes = new byte[BodyLength];
        input.readFully(bodyBytes);
        body = (T) bodyBytes;
    }


    @SuppressWarnings("unchecked")
    public T getBody(ReadMode readMode) {
        if (readMode == ReadMode.TEXT_MODE && body != null) {
            return (T) new String((byte[]) body, StandardCharsets.UTF_8);
        }
        return body;
    }

   public enum ReadMode {
        TEXT_MODE,
        BINARY_MODE

    }


}

