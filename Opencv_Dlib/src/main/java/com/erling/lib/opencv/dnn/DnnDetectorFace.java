package com.erling.lib.opencv.dnn;

import com.erling.lib.opencv.struct.output.FaceFeatureByte;
import com.erling.lib.opencv.struct.output.ImageData;
import com.erling.lib.opencv.struct.param.EncodeParam;
import com.sun.jna.Library;
import com.sun.jna.Pointer;

public interface DnnDetectorFace extends Library {


    Pointer DnnDetectorFaceCreate(String configPath);



    void DnnDetectorFaceGetFaceFeature(Pointer dnnDetectorFace,
                                       byte[] input, int inputSize, EncodeParam encodeParam,
                                       Pointer faceFeature,
                                       ImageData output, FaceFeatureByte faceFeatureByte
    );

    void DnnDetectorFaceDestroy(Pointer dnnDetectorFace);


    double  DnnDetectorFaceGetDistanceForByte(byte[] input1, byte[] input2);
}
