package com.erling.service.opencv.model.yolo;

import com.erling.lib.ann.DyLibrary;
import com.erling.lib.load.Loader;
import com.erling.lib.opencv.dnn.DnnYoloV5;
import com.erling.lib.opencv.struct.destroy.DataDestroy;
import com.erling.lib.opencv.struct.output.ImageData;
import com.erling.lib.opencv.struct.output.OutputJson;
import com.erling.service.opencv.model.sr.SR;
import com.erling.utils.tomls.ReadToml;
import com.sun.jna.Pointer;

import java.util.Map;

public class YoloV5 {
    Pointer netPtr;


    @DyLibrary(TomlPath = "/config/Lib.toml")
    interface IDnnYoloV5 extends DnnYoloV5 {}

    @DyLibrary(TomlPath = "/config/Lib.toml")
    interface IDestroy extends DataDestroy {}

    private final IDnnYoloV5 dnnYoloV5;

    private final IDestroy destroy;

    public YoloV5(){
        dnnYoloV5 = Loader.LoadTLibrary(IDnnYoloV5.class);
        destroy = Loader.LoadTLibrary(IDestroy.class);
        netPtr= dnnYoloV5.DnnYoloV5Create(
                ReadToml.TomlString("/config/Lib.toml","YOLOV5ConfigPath")
        );
    }

    public YoloV5 setSR(SR sr){
        dnnYoloV5.DnnYoloV5SetSR(netPtr,sr.getNetPtr());
        return this;
    }
    public Map<String,byte[]> DnnYoloV5DebugTime(int size, byte[] bytes){
        ImageData imageData = new ImageData();
        OutputJson outputJson = new OutputJson();
        dnnYoloV5.DnnYoloV5DebugTime(netPtr,size,bytes,imageData,outputJson);
        return Map.of(
                outputJson.getDataJsonUtf8(), imageData.getDataBuffer()
        );
    }
    public Map<String,byte[]> DnnYoloV5DebugSRTime(int size, byte[] bytes){
        ImageData imageData = new ImageData();
        OutputJson outputJson = new OutputJson();
        dnnYoloV5.DnnYoloV5DebugSRTime(netPtr,size,bytes,imageData,outputJson);
        return Map.of(
                outputJson.getDataJsonUtf8(), imageData.getDataBuffer()
        );
    }
    ImageData imageData = new ImageData();
    public Map<String,byte[]> DnnYoloV5Detection(int size, byte[] bytes){

        OutputJson outputJson = new OutputJson();
        dnnYoloV5.DnnYoloV5Detection(netPtr,size,bytes,imageData,outputJson);
//        byte[] data = imageData.getDataBuffer();
//        destroy.ImageDataDestroy(imageData);
        return Map.of(
                outputJson.getDataJsonUtf8(), imageData.getDataBuffer()
        );
    }


   public Map<String,byte[]> Detection(int size, byte[] bytes){
        ImageData imageData = new ImageData();
        OutputJson outputJson = new OutputJson();
        dnnYoloV5.DnnYoloV5Detection(netPtr,size,bytes,imageData,outputJson);
        return Map.of(
                outputJson.getDataJsonUtf8(), imageData.getDataBuffer()
        );
    }

    public void Destroy(){
        destroy.ImageDataDestroy(imageData);
        dnnYoloV5.DnnYoloV5Destroy(netPtr);
    }
}
