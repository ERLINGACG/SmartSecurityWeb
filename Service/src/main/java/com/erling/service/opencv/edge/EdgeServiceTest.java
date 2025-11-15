package com.erling.service.opencv.edge;

import com.erling.lib.instance.PathConfig;
import com.erling.lib.opencv.edge.EdgeDetection;
import com.erling.lib.instance.Instance;
import com.erling.lib.ann.LibraryAnn;
import com.erling.lib.instance.Load;
import com.erling.lib.opencv.struct.output.ImageData;
import com.erling.lib.opencv.struct.param.EdgeParam;
import com.sun.jna.Pointer;

import org.springframework.stereotype.Service;

@Service
public class EdgeServiceTest {
    private final EdgeDetection edgeDetection;
    private final Pointer EdgeClassPointer;

    @LibraryAnn(
            WindowsPath = PathConfig.WindowsPath,
            LinuxPath = PathConfig.LinuxPath
    )
    interface Edge2 extends EdgeDetection{}
    EdgeServiceTest(){
        edgeDetection= Instance.OPENCV_4120_EDGE.getInstance();
        EdgeClassPointer=edgeDetection.createEdgeDetection();
        try{
            Load load1=new Load(Edge2.class);
            Edge2 edge2=load1.loading();
            if(edge2!=null){
                System.out.println("加载成功");
            }
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    public byte[] SodelDetection(byte[] inputImg){
        ImageData OutPutData=new ImageData();
        EdgeParam param = new EdgeParam();
        param.setImageqos(95);       // 图像质量参数（默认建议值）
        param.setImageCode(".jpeg");   // 图像编码格式（根据实际输入设置）
        param.setDelta(5);
        param.setKsize(3);
        param.setScale(1.0);
        param.setGauss_size(1);
        param.setSigmaX(0.3);
        edgeDetection.SodelDetection(
                EdgeClassPointer,
                param,
                inputImg,
                inputImg.length,
                OutPutData);
        return OutPutData.getDataBuffer();
    }


}
