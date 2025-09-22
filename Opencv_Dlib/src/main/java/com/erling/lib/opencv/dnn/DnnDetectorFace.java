package com.erling.lib.opencv.dnn;

import com.erling.lib.opencv.struct.output.FaceFeatureByte;
import com.erling.lib.opencv.struct.output.ImageData;
import com.erling.lib.opencv.struct.param.EncodeParam;
import com.erling.lib.opencv.struct.param.FaceParam;
import com.sun.jna.Library;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

public interface DnnDetectorFace extends Library {
    Pointer DnnDetectorFaceCreate(FaceParam faceParam);

    Pointer DnnDetectorFaceCreate_1(String configPath);



    void DnnDetectorFaceGetFaceFeature_0(Pointer dnnDetectorFace,
                                         byte[] input, int inputSize, EncodeParam encodeParam,
                                         Pointer faceFeature,
                                         ImageData output, FaceFeatureByte faceFeatureByte
                                         );



    double  DnnDetectorFaceGetDistanceForByte(byte[] input1, byte[] input2);
}
