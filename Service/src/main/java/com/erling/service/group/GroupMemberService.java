package com.erling.service.group;

import com.erling.dao.group.GroupMemberMapper;
import com.erling.entity.group.GroupMember;
import com.erling.lib.dlib.struct.data.Output;
import com.erling.lib.dlib.struct.param.FaceNew;
import com.erling.lib.instance.Load;
import com.erling.service.exception.exc.MemberBusinessException;
import com.erling.service.group.dlib.FacialRecognitionE;
import com.erling.service.obj.ServiceObject;
import com.erling.service.opencv.dnn.CVDnnFaceService;
import com.erling.utils.log.Logger;
import com.erling.utils.result.Result;
import com.erling.utils.result.ResultEnum;
import com.erling.utils.result.ren.MemberResultEnum;
import com.sun.jna.Pointer;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;

@Service
public class GroupMemberService extends ServiceObject {

    GroupMemberMapper  groupMemberMapper;
    CVDnnFaceService  cvdnnFaceService;

    FacialRecognitionE facialRecognitionE;
    Pointer faceRec;
    Load RF = new Load(FacialRecognitionE.class);

    @Deprecated
    public void InitDLIB(){
        try{
            this.facialRecognitionE = RF.loading();
            FaceNew faceNew=new FaceNew();
            faceNew.predictor_path="lib/x64/debug/shape_predictor_68_face_landmarks.dat";
            faceNew.recognition_Path="lib/x64/debug/dlib_face_recognition_resnet_model_v1.dat";
            faceRec=facialRecognitionE.createFacialRecognition(faceNew);
        }catch(Exception e){
            Logger.getLogger(GroupMemberService.class).error("加载模型失败",e);
        }
    }

    public GroupMemberService(GroupMemberMapper groupMemberMapper,CVDnnFaceService cvdnnFaceService) {
        this.groupMemberMapper = groupMemberMapper;
        this.cvdnnFaceService = cvdnnFaceService;
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Result<?>> addGroupMember(GroupMember groupMember, byte[] imageInput) {

             groupMember.setUpdateTime(LocalDateTime.now());
             byte[] feature = cvdnnFaceService.getFeatureForByte(imageInput);
             if(feature.length==0){
                 throw new MemberBusinessException(
                         MemberResultEnum.MEMBER_DETECT_NOT_FACE
                 );
             }
             groupMember.setMemberFeature(feature);
             return ResponseEntity.ok(
                     new Result<>(
                             MemberResultEnum.MEMBER_ADD_SUCCESS,
                             groupMemberMapper.insertGroupMember(groupMember)
                     )
             );

    }

    @Deprecated
    public ResponseEntity<Result<?>> verifyGroupMemberMysql(int gid, byte[] imageInput) {
        HashMap<String,Object> map=new HashMap<>();
        Output output=new Output();
        facialRecognitionE.getDetection(faceRec,imageInput,imageInput.length,output);
            if(output.size==0){
                 return ResponseEntity.ok(
                         new Result<>(
                                 ResultEnum.MEMBER_VERIFY_FACES_ISNULL,
                                 null
                         )
                 );
             }
        for(GroupMember groupMember:groupMemberMapper.selectGroupMembersALL(gid)){

            double distance =   facialRecognitionE.getDistance(
                          faceRec,
                          output.getBuffer(),
                          output.getBuffer().length,
                          groupMember.getMemberFeature(),
                          groupMember.getMemberFeature().length
               );

                 if(distance<0.6){
                   map.put("memberName",groupMember.getMemberName());
                   map.put("distance",distance);
                   return ResponseEntity.ok(
                           new Result<>(
                                   ResultEnum.MEMBER_VERIFY_SUCCESS,
                                   map
                           )
                   );
               }
             }
             return ResponseEntity.ok(
                     new Result<>(
                             ResultEnum.MEMBER_VERIFY_FACES_ISNULL,
                             map
                     )
             );

    }

    public ResponseEntity<Result<?>> getGroupMembers(int gid){
        return ResponseEntity.ok(
                new Result<>(200,
                            "获取成功",
                            groupMemberMapper.selectGroupMembers(gid)
                )
        );
    }
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Result<?>> updateGroupMember(GroupMember groupMember, MultipartFile file) throws IOException {
            byte[] feature = cvdnnFaceService.getFeatureForByte(file.getBytes());

            if(feature.length==0){
                throw new MemberBusinessException(
                        MemberResultEnum.MEMBER_DETECT_NOT_FACE
                );
            }
            groupMember.setMemberFeature(feature);
            groupMember.setUpdateTime(LocalDateTime.now());
            return ResponseEntity.ok(
                    new Result<>(200,
                            "更新成功",
                            groupMemberMapper.updateGroupMember(groupMember)
                    )
            );
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Result<?>> updateGroupMemberNoFeatures(GroupMember groupMember){
        groupMember.setUpdateTime(LocalDateTime.now());
        return ResponseEntity.ok(
                new Result<>(200,
                            "更新成功",
                            groupMemberMapper.updateGroupMemberNoFeatures(groupMember)
                )
        );
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Result<?>> deleteGroupMember(int gid,int mid){
            boolean isDelete=groupMemberMapper.deleteGroupMember(gid,mid);
            if(!isDelete){
                throw new MemberBusinessException(
                        MemberResultEnum.MEMBER_DELETE_NOT_EXIST
                );
            }
            return ResponseEntity.ok(
                    new Result<>(200,
                            "删除成功",
                            true
                    )
            );

    }

}
