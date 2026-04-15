package com.erling.service.opencv.utils;

import com.erling.lib.instance.Instance;
import com.erling.lib.opencv.struct.output.ImageData;
import com.erling.lib.opencv.utils.Utils;
import com.erling.utils.log.Logger;

//@Service
public class UtilsServiceTest {
    Utils utils;
    public UtilsServiceTest() {
        try{
            utils = Instance.OPENCV_4120_UTILS.getInstance();
        }catch(Exception e){
            Logger.getLogger(UtilsServiceTest.class).error("加载模型失败",e);
        }

    }
    public byte[] Test1(byte[] input){
        long startTime = System.currentTimeMillis();  // 新增：记录开始时间
        ImageData OutPutData = new ImageData();
        utils.ResizeWithPadding(
                input,
                input.length,
                400,
                400,
                 OutPutData
         );
        long endTime = System.currentTimeMillis();  // 新增：记录结束时间
        System.out.println("Test1 执行耗时: " + (endTime - startTime) + "ms");
        return OutPutData.getDataBuffer();
    }
}
