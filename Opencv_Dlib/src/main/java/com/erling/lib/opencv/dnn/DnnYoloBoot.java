package com.erling.lib.opencv.dnn;

import com.erling.lib.opencv.struct.output.ImageData;
import com.erling.lib.opencv.struct.output.OutputJson;
import com.sun.jna.Library;
import com.sun.jna.Pointer;

public interface DnnYoloBoot extends Library {

    Pointer DnnYoloBootCreate(String configPath);

    void  DnnYoloBootDestroy(Pointer ptr);

    void DnnYoloBootDetection(Pointer ptr, int size, byte[] bytes, ImageData output,
                              OutputJson outputJson);
}
