package com.erling.lib.dlib.face;

import com.erling.lib.dlib.struct.data.Output;
import com.erling.lib.dlib.struct.param.FaceNew;
import com.erling.lib.ann.LibraryAnn;
import com.erling.lib.instance.Load;
import com.erling.lib.instance.PathConfig;
import com.sun.jna.Pointer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FacialRecognitionTest {

    @LibraryAnn(
            WindowsPath = PathConfig.WindowsPath,
            LinuxPath =   PathConfig.LinuxPath
    )
    interface  faceTest extends FacialRecognition{}

    faceTest faceTest1=Load.loading(faceTest.class);
    public void test() throws IOException {
        FaceNew faceNew=new FaceNew();
        Output output=new Output();
        Output output1=new Output();
        faceNew.predictor_path="lib/x64/debug/shape_predictor_68_face_landmarks.dat";
        faceNew.recognition_Path="lib/x64/debug/dlib_face_recognition_resnet_model_v1.dat";
        Pointer f1=faceTest1.createFacialRecognition(
                faceNew
        );
        File imageFile = new File("E:\\SmartSecurity\\SmartSecurityWeb\\lib\\example\\test3.png");
        File imageFile1 = new File("E:\\SmartSecurity\\SmartSecurityWeb\\lib\\example\\test3.png");
        byte[] img= Files.readAllBytes(imageFile.toPath());
        byte[] img1= Files.readAllBytes(imageFile1.toPath());
        System.out.println(img.length);

        faceTest1.getDetection(f1,img,img.length,output);
        faceTest1.getDetection(f1,img1,img1.length,output1);

       double distance= faceTest1.getDistance(f1,
                output.getBuffer(),
                output.size,
                output1.getBuffer(),
                output1.size);
       System.out.println("距离: "+distance);
//        ByteBuffer byteBuffer = output.buffer.getByteBuffer(0, output.size);
//        byteBuffer.order(ByteOrder.nativeOrder()); // 保持与C++端相同的字节序
//        FloatBuffer floatBuffer = byteBuffer.asFloatBuffer();
//
//        float[] features = new float[128];
//        floatBuffer.get(features);
//
//        System.out.println("特征值示例: " + Arrays.toString(Arrays.copyOf(features, 128)));

    }

    public static void main(String[] args) throws IOException {
        FacialRecognitionTest facialRecognitionTest=new FacialRecognitionTest();
        facialRecognitionTest.test();
    }
}
