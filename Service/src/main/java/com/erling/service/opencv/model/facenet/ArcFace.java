package com.erling.service.opencv.model.facenet;

import com.erling.lib.ann.DyLibrary;
import com.erling.lib.load.Loader;
import com.erling.lib.opencv.dnn.DnnArcFace;
import com.erling.utils.tomls.ReadToml;
import com.sun.jna.Pointer;
import lombok.Getter;

public class ArcFace {
    @DyLibrary(TomlPath = "/config/Lib.toml")
    interface IDnnArcFace extends DnnArcFace {}

    @Getter
    final Pointer netPtr;

    final IDnnArcFace IDnnArcFace;
    public ArcFace() {
        IDnnArcFace = Loader.LoadTLibrary(IDnnArcFace.class);
        netPtr = IDnnArcFace.DnnArcFaceCreate(
                ReadToml.TomlString("/config/Lib.toml","ArcFacePath")
        );
    }

    public double VerifyFeature(byte[] input1, byte[] input2){
        return IDnnArcFace.DnnArcFaceVerifyFeature(netPtr,input1,input2);
    }

    public void Destroy(){
        IDnnArcFace.DnnArcFaceDestroy(netPtr);
    }
}
