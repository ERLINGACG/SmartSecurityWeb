package com.erling.service.opencv.model.ssd;

import com.erling.lib.ann.DyLibrary;
import com.erling.lib.load.Loader;
import com.erling.lib.opencv.dnn.DnnSSDcaffem;
import com.erling.lib.opencv.struct.output.FaceFeatureByte;
import com.erling.lib.opencv.struct.output.ImageData;
import com.erling.lib.opencv.struct.output.OutputJson;
import com.erling.service.opencv.model.facenet.FaceNet;
import com.erling.utils.tomls.ReadToml;
import com.sun.jna.Pointer;

import java.util.Map;

public class SSDcaffem {


    FaceNet faceNet = new FaceNet();
    Pointer netPtr;
    @DyLibrary(TomlPath = "/config/Lib.toml")
    interface  IDnnSSDcaffem extends DnnSSDcaffem {};


    final IDnnSSDcaffem dnnSSDcaffem;


    public SSDcaffem() {
        dnnSSDcaffem = Loader.LoadTLibrary(IDnnSSDcaffem.class);
        netPtr = dnnSSDcaffem.DnnSSDcaffemCreate(
                ReadToml.TomlString("/config/Lib.toml","SSDConfigPath"
                ),
                faceNet.getNetPtr()
        );
    }

    public Map<String,byte[]> DnnSSDcaffemDetection(int size, byte[] bytes){
        ImageData imageData = new ImageData();
        OutputJson outputJson = new OutputJson();
        dnnSSDcaffem.DnnSSDcaffemDebugTime(netPtr,size,bytes,imageData,outputJson);
        return Map.of(
                outputJson.getDataJsonUtf8(), imageData.getDataBuffer()
        );
    }
    public byte[] DnnSSDcaffemDetectionFeature(int size, byte[] bytes){
        ImageData imageData = new ImageData();
        OutputJson outputJson = new OutputJson();
        FaceFeatureByte feature = new FaceFeatureByte();
        dnnSSDcaffem.DetectionFeatureByte(netPtr,size,bytes,imageData,outputJson,feature);
        return feature.getData();
    }

    public double VerifyFeature(byte[] feature1,byte[] feature2){
        return faceNet.VerifyFeature(feature1,feature2);
    }



}
