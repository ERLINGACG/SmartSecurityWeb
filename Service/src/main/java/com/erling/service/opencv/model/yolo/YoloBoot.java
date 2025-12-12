package com.erling.service.opencv.model.yolo;

import com.erling.lib.ann.DyLibrary;
import com.erling.lib.load.Loader;
import com.erling.lib.opencv.dnn.DnnYoloBoot;
import com.erling.lib.opencv.struct.output.ImageData;
import com.erling.lib.opencv.struct.output.OutputJson;
import com.erling.utils.tomls.ReadToml;
import com.sun.jna.Pointer;

import java.util.Map;

public class YoloBoot extends YoloV5 {


    Pointer netPtr;
    @DyLibrary(TomlPath = "/config/Lib.toml")
    interface IDnnYolo extends DnnYoloBoot {}

    private final IDnnYolo dnnYolo;

    public YoloBoot(){
        dnnYolo = Loader.LoadTLibrary(IDnnYolo.class);
        netPtr = dnnYolo.DnnYoloBootCreate(ReadToml.TomlString("/config/Lib.toml","YOLOBootPath"));
    }

    public void Destroy(){
        dnnYolo.DnnYoloBootDestroy(netPtr);
    }

    public Map<String,byte[]> DnnYoloDetection(int size, byte[] bytes){
        ImageData imageData = new ImageData();
        OutputJson outputJson = new OutputJson();
        dnnYolo.DnnYoloBootDetection(netPtr,size,bytes,imageData,outputJson);
        return Map.of(
                outputJson.getDataJsonUtf8(), imageData.getDataBuffer()
        );
    }

    @Override
    public Map<String,byte[]> Detection(int size, byte[] bytes){
        ImageData imageData = new ImageData();
        OutputJson outputJson = new OutputJson();
        dnnYolo.DnnYoloBootDetection(netPtr,size,bytes,imageData,outputJson);
        return Map.of(
                outputJson.getDataJsonUtf8(), imageData.getDataBuffer()
        );
    }
}
