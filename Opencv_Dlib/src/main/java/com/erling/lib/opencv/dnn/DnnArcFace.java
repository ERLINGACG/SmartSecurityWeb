package com.erling.lib.opencv.dnn;

import com.sun.jna.Library;
import com.sun.jna.Pointer;

public interface DnnArcFace extends Library {
    Pointer DnnArcFaceCreate(String env);

    void DnnArcFaceDestroy(Pointer ptr);

    double DnnArcFaceVerifyFeature(Pointer ptr, byte[] input1, byte[] input2);
}
