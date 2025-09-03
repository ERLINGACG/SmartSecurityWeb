package com.erling.lib.opencv.dnn;

import com.erling.lib.instance.Instance;
import com.erling.lib.opencv.struct.output.ImageData;
import com.sun.jna.Pointer;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class DnnTest {

    static DnnDetector dnnDetector= Instance.OPENCV_4120_DNN.getInstance();;
    static Pointer netClass=dnnDetector.createDnnDetector("lib/x64/debug/best.onnx",0.5,0.5);

    static public byte[] DnnD1(byte[] image){
        ImageData data=new ImageData();
        dnnDetector.DnnDetectorYolo(netClass,image,image.length,data);
        return data.getDataBuffer();

    }
    // 方法1：使用FileInputStream和ByteArrayOutputStream
    public static byte[] convertImageToByteArray(String filePath) throws IOException {
        try (FileInputStream fis = new FileInputStream(filePath);
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
            return bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static public void main(String[] args) throws IOException {
        byte[] image=convertImageToByteArray("E:\\SmartSecurity\\SmartSecurityWeb\\lib\\example\\i1.jpg");
        byte[] result=DnnD1(image);
        String outputPath = "./result.jpg";
        try (FileOutputStream fos = new FileOutputStream(outputPath)) {
            fos.write(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
