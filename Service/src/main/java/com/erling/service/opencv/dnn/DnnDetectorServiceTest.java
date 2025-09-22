package com.erling.service.opencv.dnn;

import com.erling.lib.opencv.dnn.DnnDetector;
import com.erling.lib.instance.Instance;
import com.erling.lib.opencv.struct.output.ImageData;
import com.sun.jna.Pointer;
import org.springframework.stereotype.Service;

@Service
public class DnnDetectorServiceTest {

    DnnDetector dnnDetector=Instance.OPENCV_4120_DNN.getInstance();;

//     Pointer netClass=dnnDetector.createDnnDetector("lib/x64/debug/best.onnx",
//             0.4,0.15);
    Pointer netClass=dnnDetector.createDnnDetector_1("lib/x64/debug/config/yoloConfig.json");

    public byte[] detectTest(byte[] image){
        ImageData imageData=new ImageData();
        long startTime = System.currentTimeMillis();  // 记录开始时间
        dnnDetector.DnnDetectorYolo(netClass,image,image.length,imageData);
        long endTime = System.currentTimeMillis();  // 记录结束时间
        System.out.println("耗时：" + (endTime - startTime) + "ms");
        return imageData.getDataBuffer();
    }


}
