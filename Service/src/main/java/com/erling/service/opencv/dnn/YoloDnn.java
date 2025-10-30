package com.erling.service.opencv.dnn;

import com.erling.lib.instance.Instance;
import com.erling.lib.instance.LibraryAnn;
import com.erling.lib.instance.Load;
import com.erling.lib.instance.PathConfig;
import com.erling.lib.opencv.dnn.DnnDetector;
import com.erling.lib.opencv.struct.output.ImageData;
import com.erling.lib.opencv.struct.output.OutputJson;
import com.sun.jna.Pointer;

import java.util.Map;

public class YoloDnn {

    static DnnDetector dnnDetector= Instance.OPENCV_4120_DNN.getInstance();;
    static Pointer netClass=dnnDetector.CreateDnnDetector("./lib/x64/debug/config/yoloConfig.json");
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
        dnnDetector.DnnDetectorYolo(netClass2,img,img.length,data,outputJson);
        if(outputJson.getDataJsonUtf8()==null){
            return null;
        }
        return Map.of(outputJson.getDataJsonUtf8(),data.getDataBuffer());
    }

    public void Destroy(){
        dnnDetector.DestroyDnnDetector(netClass2);
    }



//    public static void main(String[] args) throws IOException {
//        Path imagePath = Paths.get("E:\\SmartSecurity\\SmartSecurityWeb\\received_images\\img_1753069312963.jpg");
//        byte[] imageData = Files.readAllBytes(imagePath);
//        ImageData data=new ImageData();
//        dnnDetector.DnnDetectorYolo(netClass,imageData,imageData.length,data);
//    }

}
