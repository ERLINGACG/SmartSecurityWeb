package com.erling.service.opencv.model.facenet;

import com.erling.lib.ann.DyLibrary;
import com.erling.lib.load.Loader;
import com.erling.lib.opencv.dnn.DnnFaceNet;
import com.erling.utils.tomls.ReadToml;
import com.sun.jna.Pointer;
import lombok.Getter;

public class FaceNet {
    @DyLibrary(TomlPath = "/config/Lib.toml")
    interface IDnnFaceNet extends DnnFaceNet {};
    @Getter
    final Pointer netPtr;

    final IDnnFaceNet dnnFaceNet;
    public FaceNet() {
        dnnFaceNet = Loader.LoadTLibrary(IDnnFaceNet.class);
        netPtr = dnnFaceNet.DnnFaceNetCreate(
                ReadToml.TomlString("/config/Lib.toml","FaceNetPath")
        );
    }

    public double VerifyFeature(byte[] input1, byte[] input2){
        return dnnFaceNet.DnnFaceNetVerifyFeature(netPtr,input1,input2);
    }
}
