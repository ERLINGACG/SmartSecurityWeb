package com.erling.lib.opencv.dnn;

import com.erling.lib.opencv.struct.param.FaceFeatureParam;
import com.sun.jna.Library;
import com.sun.jna.Pointer;

public interface DnnFeatureFace extends Library {
    Pointer DnnFeatureFaceCreate(String config);

    void DnnFeatureFaceDestroy(Pointer detector);
}
