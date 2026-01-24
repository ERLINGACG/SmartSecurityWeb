package com.erling.lib.opencv.dnn;

import com.erling.lib.opencv.struct.output.FaceFeatureByte;
import com.erling.lib.opencv.struct.output.ImageData;
import com.erling.lib.opencv.struct.output.OutputJson;
import com.sun.jna.Library;
import com.sun.jna.Pointer;

public interface DnnYuNet extends Library {

    Pointer DnnYuNetCreate(String modelPath);

    void DnnYuNetDestroy(Pointer netPtr);

    void DnnYuNetGetInfo(Pointer netPtr);

    void DnnYuNetSetArc(Pointer netPtr, Pointer arcFacePtr);

    double DnnYuNetVerifyFeature(byte[] imageFeature, byte[] feature);

    void DnnYuNetDebugTime(Pointer ptr, int size, byte[] bytes, ImageData output,
                           OutputJson outputJson, FaceFeatureByte feature);
}
