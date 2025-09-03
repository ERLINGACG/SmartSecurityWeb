package com.erling.service.opencv.dnn;

import com.erling.lib.dlib.struct.data.Output;
import com.erling.lib.instance.Instance;
import com.erling.lib.opencv.dnn.DnnDetector;
import com.erling.lib.opencv.struct.output.ImageData;
import com.sun.jna.Pointer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class YoloDnnTest {

    static DnnDetector dnnDetector= Instance.OPENCV_4120_DNN.getInstance();;
    static Pointer netClass=dnnDetector.createDnnDetector("lib/x64/debug/best2.onnx",0.5,0.15);

    public static byte[] TEST_D(byte[] img,int length){
        ImageData data=new ImageData();
        dnnDetector.DnnDetectorYolo(netClass,img,length,data);
        return data.getDataBuffer();
    }

    public static void main(String[] args) throws IOException {
        Path imagePath = Paths.get("E:\\SmartSecurity\\SmartSecurityWeb\\received_images\\img_1753069312963.jpg");
        byte[] imageData = Files.readAllBytes(imagePath);
        ImageData data=new ImageData();
        dnnDetector.DnnDetectorYolo(netClass,imageData,imageData.length,data);
    }

}
