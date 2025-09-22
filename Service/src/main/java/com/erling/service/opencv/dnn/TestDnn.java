package com.erling.service.opencv.dnn;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

public class TestDnn {
    YoloDnnTest yoloDnnTest=new YoloDnnTest();



    public  void testDnn() throws IOException {
        File file=new File("E:\\SmartSecurity\\PyTest\\webtest\\image\\test3.jpg");
        byte[] bytes= Files.readAllBytes(file.toPath());
        Map<String,byte[]> result=yoloDnnTest.TEST_D3(bytes);
        System.out.println(result.keySet().iterator().next());
        System.out.println(result.values().iterator().next().length);
    }

//    public static void main(String[] args) throws IOException {
//        new  TestDnn().testDnn();
//    }
}
