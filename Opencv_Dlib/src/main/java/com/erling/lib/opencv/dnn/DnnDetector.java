package com.erling.lib.opencv.dnn;

import com.erling.lib.opencv.struct.output.ImageData;
import com.erling.lib.opencv.struct.output.OutputJson;
import com.sun.jna.Library;
import com.sun.jna.Pointer;

public interface DnnDetector extends Library {
    Pointer createDnnDetector(String modelPath,double confidence,double nms);
    void DnnDetectorYolo(
            Pointer dnnDetector ,
            byte[] input,
            int size,
            ImageData output
    );
    void DnnDetectorYolo(
            Pointer dnnDetector ,
            byte[] input,
            int size,
            ImageData output,
            OutputJson outputJson
    ) ;

}
