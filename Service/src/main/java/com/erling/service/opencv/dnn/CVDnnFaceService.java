package com.erling.service.opencv.dnn;

import com.erling.dao.group.GroupMemberMapper;
import com.erling.entity.group.GroupMember;
import com.erling.lib.instance.LibraryAnn;
import com.erling.lib.instance.Load;
import com.erling.lib.instance.PathConfig;
import com.erling.lib.opencv.dnn.DnnDetectorFace;
import com.erling.lib.opencv.dnn.DnnFeatureFace;
import com.erling.lib.opencv.struct.output.FaceFeatureByte;
import com.erling.lib.opencv.struct.output.ImageData;
import com.erling.lib.opencv.struct.param.EncodeParam;
import com.erling.lib.opencv.struct.param.FaceFeatureParam;
import com.erling.lib.opencv.struct.param.FaceParam;
import com.sun.jna.Pointer;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CVDnnFaceService {

    GroupMemberMapper groupMemberMapper;
    @LibraryAnn(
            WindowsPath = PathConfig.WindowsPath,
            LinuxPath = PathConfig.LinuxPath
    )
    public interface DnnFI_1 extends DnnDetectorFace{}
    @LibraryAnn(
            WindowsPath = PathConfig.WindowsPath,
            LinuxPath = PathConfig.LinuxPath
    )
    public interface DnnFI_2 extends DnnFeatureFace {}

    DnnFI_1 dnnFI_1;
    DnnFI_2 dnnFI_2;

    Pointer faceDetectorNet;
    Pointer faceFeatureExtractor;
    FaceParam faceParam=new FaceParam();

    EncodeParam encodeParam=new EncodeParam(95, ".jpeg");
    FaceFeatureParam  faceFeatureParam=new FaceFeatureParam();
    CVDnnFaceService(GroupMemberMapper groupMemberMapper) {
      this.groupMemberMapper = groupMemberMapper;
      dnnFI_1 = Load.loading(DnnFI_1.class);
      dnnFI_2 = Load.loading(DnnFI_2.class);

      this.faceParam.setCuda(true);
      this.faceFeatureParam.setCuda(true);

      this.faceParam.setCaffemodel_path(
                "E:\\SmartSecurity\\SmartSecurityWeb\\lib\\x64\\debug\\model\\testN\\res10_300x300_ssd_iter_140000.caffemodel");
      this.faceParam.setPrototxt_path(
                "E:\\SmartSecurity\\SmartSecurityWeb\\lib\\x64\\debug\\model\\testN\\test.prototxt");

      this.faceFeatureParam.setFacenet_path(
                "lib/x64/debug/model/facenet.onnx");
      faceDetectorNet = dnnFI_1.DnnDetectorFaceCreate_1("lib/x64/debug/config/coffeConfig.json");
      faceFeatureExtractor = dnnFI_2.DnnFeatureFaceCreate(faceFeatureParam);
    }




    public byte[] getFeatureForByte(byte[] imageData) { //获取人脸特征
        ImageData image = new ImageData();
        FaceFeatureByte faceFeatureByte = new FaceFeatureByte();
        dnnFI_1.DnnDetectorFaceGetFaceFeature_0(
                faceDetectorNet,
                imageData, imageData.length,encodeParam,
                faceFeatureExtractor,image,
                faceFeatureByte
        );
        return faceFeatureByte.getData();

    }
    public byte[] getImageForByte(byte[] imageData) { //获取人脸特征
        ImageData image = new ImageData();
        FaceFeatureByte faceFeatureByte = new FaceFeatureByte();
        dnnFI_1.DnnDetectorFaceGetFaceFeature_0(
                faceDetectorNet,
                imageData, imageData.length,encodeParam,
                faceFeatureExtractor,image,
                faceFeatureByte
        );
        return image.getDataBuffer();
    }
    public double getDistance(int gid,byte[] feature_img) { //计算两张人脸特征的距离
        double distance=0;
        ImageData image = new ImageData();
        FaceFeatureByte faceFeatureByte = new FaceFeatureByte();
        dnnFI_1.DnnDetectorFaceGetFaceFeature_0(
                faceDetectorNet,
                feature_img, feature_img.length,encodeParam,
                faceFeatureExtractor,image,
                faceFeatureByte
        );
        List<GroupMember> groupMembers = groupMemberMapper.selectGroupMembersALL(gid);
        int count=0;
        for(GroupMember groupMember:groupMembers){
            byte[] feature = groupMember.getMemberFeature();
            System.out.println(feature.length);
            distance = dnnFI_1.DnnDetectorFaceGetDistanceForByte(faceFeatureByte.getData(), feature);
            count++;
            System.out.println("name:"+groupMember.getMemberName());
            System.out.println("distance:"+distance);
            if(distance<0.6){
                System.out.println("第"+count+"个距离小于0.6,具体距离:"+distance);
                return distance;
            }
        }
        System.out.println("距离大于0.6:"+distance);
        return distance;
    }
}