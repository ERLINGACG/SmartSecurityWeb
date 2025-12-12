package com.erling.lib.opencv.dnn;

import com.erling.lib.opencv.struct.output.FaceFeatureByte;
import com.erling.lib.opencv.struct.output.ImageData;
import com.erling.lib.opencv.struct.output.OutputJson;
import com.sun.jna.Library;
import com.sun.jna.Pointer;

public interface DnnSSDcaffem extends Library {
    Pointer DnnSSDcaffemCreate(String configPath,Pointer netPtr);

    void DnnSSDcaffemDestroy(Pointer ptr);

    void  DnnSSDcaffemDebugTime(Pointer ptr, int size, byte[] bytes, ImageData output,
                                OutputJson outputJson);

    void DetectionFeatureByte(Pointer ptr, int size, byte[] bytes, ImageData output,
                              OutputJson outputJson, FaceFeatureByte feature);
}
