package com.erling.lib.opencv.dnn;

import com.sun.jna.Library;
import com.sun.jna.Pointer;

public interface DnnSR extends Library {

    Pointer DnnSRCreate(String env);
}
