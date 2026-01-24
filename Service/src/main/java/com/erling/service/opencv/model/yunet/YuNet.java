package com.erling.service.opencv.model.yunet;

import com.erling.lib.ann.DyLibrary;
import com.erling.lib.load.Loader;
import com.erling.lib.opencv.dnn.DnnYuNet;
import com.erling.lib.opencv.struct.output.FaceFeatureByte;
import com.erling.lib.opencv.struct.output.ImageData;
import com.erling.lib.opencv.struct.output.OutputJson;
import com.erling.service.opencv.model.facenet.ArcFace;
import com.erling.utils.tomls.ReadToml;
import com.sun.jna.Pointer;

public class YuNet {


    @DyLibrary(TomlPath = "/config/Lib.toml")
    interface  IDnnYuNet extends DnnYuNet {};
    final IDnnYuNet dnnYuNet;

    final Pointer netPtr;
    public YuNet(ArcFace arcFace) {
        dnnYuNet = Loader.LoadTLibrary(IDnnYuNet.class);
        netPtr = dnnYuNet.DnnYuNetCreate(
                ReadToml.TomlString("/config/Lib.toml","YuNetPath")
        );
        dnnYuNet.DnnYuNetSetArc(netPtr, arcFace.getNetPtr());

    }

    public byte[] DnnYuNetDebugTime(int size, byte[] bytes) {
        ImageData imageData = new ImageData();
        OutputJson outputJson = new OutputJson();
        FaceFeatureByte feature = new FaceFeatureByte();
        dnnYuNet.DnnYuNetDebugTime(netPtr, size, bytes, imageData, outputJson, feature);
//        System.out.println("invoke:"+imageData.getDataBuffer().length);
        return feature.getData();
    }

    public  void getInfo(){
        dnnYuNet.DnnYuNetGetInfo(netPtr);
    }

    public double VerifyFeature(byte[] imageFeature, byte[] feature) {
        return dnnYuNet.DnnYuNetVerifyFeature(imageFeature, feature);
    }
}
