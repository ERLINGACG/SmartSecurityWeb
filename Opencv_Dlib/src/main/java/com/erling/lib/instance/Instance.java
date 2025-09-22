package com.erling.lib.instance;

import com.erling.lib.dlib.face.FacialRecognition;
import com.erling.lib.opencv.dnn.DnnDetector;
import com.erling.lib.opencv.edge.EdgeDetection;
import com.erling.lib.opencv.utils.Utils;
import com.sun.jna.Library;
import com.sun.jna.Native;
import lombok.Getter;

@Getter
public enum Instance {

    OPENCV_4120_EDGE(InstanceConfig.OPENCV_4120_RELEASE, EdgeDetection.class),
    OPENCV_4120_DNN(InstanceConfig.OPENCV_4120_RELEASE, DnnDetector.class),
    OPENCV_4120_UTILS(InstanceConfig.OPENCV_4120_RELEASE, Utils.class),

    DLIB_1940_FACE(InstanceConfig.OPENCV_4120_RELEASE, FacialRecognition.class),
    ;

    private final String path;
    private final Class<? extends Library> nativeClass;
    private Library instance;
    Instance(InstanceConfig  config, Class<? extends Library> nativeClass) {
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            this.path = config.getWindowsPath();
        }else {
            this.path = config.getLinuxPath();
        }
        this.nativeClass = nativeClass;
    }

    @SuppressWarnings("unchecked")
    public <T extends Library> T getInstance() {
        if (instance == null) {
            instance = Native.load(this.path,this.nativeClass);
        }
        return (T) instance;
    }
}
