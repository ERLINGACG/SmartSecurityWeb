package com.erling.service.opencv.dnn;

import com.erling.lib.ann.LibraryAnn;
import com.erling.lib.instance.Load;
import com.erling.lib.instance.PathConfig;
import com.erling.lib.opencv.dnn.DnnDetector;
import com.erling.lib.opencv.struct.output.ImageData;
import com.erling.lib.opencv.struct.output.OutputJson;
import com.sun.jna.Pointer;

import java.util.Map;

public class YoloDnn {

//    static DnnDetector dnnDetector= Instance.OPENCV_4120_DNN.getInstance();

    @LibraryAnn(
            WindowsPath = PathConfig.WindowsPath,
            LinuxPath = PathConfig.LinuxPath
    )
    interface DnnDetect_3 extends DnnDetector {}
    DnnDetect_3 dnnDetect_3;
    Pointer netClass2;

    public YoloDnn(){
        this.dnnDetect_3= Load.loading(DnnDetect_3.class);
        this.netClass2=dnnDetect_3.CreateDnnDetector("./lib/x64/debug/config/yoloConfig.json");
    }

    public Map<String,byte[]> TEST_D3(byte[] img){
        ImageData data=new ImageData();
        OutputJson outputJson=new OutputJson();
        dnnDetect_3.DnnDetectorYolo(netClass2,img,img.length,data,outputJson);
        if(outputJson.getDataJsonUtf8()==null){
            return null;
        }
        return Map.of(outputJson.getDataJsonUtf8(),data.getDataBuffer());
    }

    public void Destroy(){
        dnnDetect_3.DestroyDnnDetector(netClass2);
    }




}
