package com.erling.service.group;

import com.erling.dao.group.GroupMapper;
import com.erling.dao.group.GroupMemberMapper;
import com.erling.entity.group.Group;
import com.erling.entity.group.GroupMember;
import com.erling.lib.dlib.struct.data.Output;
import com.erling.lib.dlib.struct.param.FaceNew;
import com.erling.lib.instance.Load;
import com.erling.service.exception.exc.MemberBusinessException;
import com.erling.service.group.dlib.FacialRecognitionE;
import com.erling.service.obj.ServiceObject;
import com.erling.service.opencv.model.facenet.ArcFace;
import com.erling.service.opencv.model.ssd.SSDcaffem;
import com.erling.service.opencv.model.yunet.YuNet;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GroupMemberService extends ServiceObject {

    GroupMemberMapper  groupMemberMapper;
//    CVDnnFaceService  cvdnnFaceService;

    GroupMapper  groupMapper;
    SSDcaffem ssdcaffem;

    YuNet  yunet;

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

    public GroupMemberService(GroupMemberMapper groupMemberMapper,GroupMapper  groupMapper) {
        this.groupMemberMapper = groupMemberMapper;
//        this.cvdnnFaceService = cvdnnFaceService;
        this.groupMapper = groupMapper;
        this.ssdcaffem = new SSDcaffem();
        this.yunet = new YuNet(new ArcFace());
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Result<?>> addGroupMember(GroupMember groupMember, byte[] imageInput) {

             groupMember.setUpdateTime(LocalDateTime.now());
//             byte[] feature = ssdcaffem.DnnSSDcaffemDetectionFeature(imageInput.length,imageInput);
             byte[] feature = yunet.DnnYuNetDebugTime(imageInput.length,imageInput);
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
                throw new MemberBusinessException(
                        MemberResultEnum.MEMBER_DETECT_NOT_FACE
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

    public ResponseEntity<Result<?>> verifyGroupMemberDnnSSDcaffem(String topic, byte[] imageInput) {
//        byte[] imageFeature = ssdcaffem.DnnSSDcaffemDetectionFeature(imageInput.length,imageInput);
        byte[] imageFeature = yunet.DnnYuNetDebugTime(imageInput.length,imageInput);
        if(imageFeature.length==0){
                throw new MemberBusinessException(
                        MemberResultEnum.MEMBER_DETECT_NOT_FACE
                );
        }
        List<Group> groupList = groupMapper.getGroupsByTopic(topic);
        double minDistance = Double.MAX_VALUE;
        GroupMember minDistanceMember = null; // 记录最小距离对应的成员
        for(Group group:groupList){
            List<GroupMember> groupMemberList = groupMemberMapper.selectGroupMembersALL(group.getGid());
            if (groupMemberList.isEmpty()) {
                continue; // 跳过无成员的群组
            }
            for(GroupMember groupMember:groupMemberList){
                byte[] feature = groupMember.getMemberFeature();
                if(feature!=null){
                    double distance = yunet.VerifyFeature(imageFeature,feature);
                    logger.info("distance:{},memberName:{}",distance,groupMember.getMemberName());
                    if (distance < minDistance) {
                        minDistance = distance;
                        minDistanceMember = groupMember.clearFeature(); // 清除特征后记录
                    }
                }
            }
        }
        if (minDistanceMember != null) {
            log.info("minDistance:{},memberName:{}",minDistance,minDistanceMember);
            return ResponseEntity.ok(
                    new Result<>(MemberResultEnum.MEMBER_GET_SUCCESS,
                                Map.of("memberName",minDistanceMember,"distance",minDistance)
                    )
            );
        } else {
           throw new MemberBusinessException(
                   MemberResultEnum.MEMBER_NOT_FOUND
           );
        }
    }

    public ResponseEntity<Result<?>> getGroupMembers(int gid){
        return ResponseEntity.ok(
                new Result<>(MemberResultEnum.MEMBER_GET_SUCCESS,
                            groupMemberMapper.selectGroupMembers(gid)
                )
        );
    }

    public ResponseEntity<Result<?>> getGroupMembersEmail(String email){
        List<Group> groupList = groupMapper.getGroups(email);
        if(groupList.isEmpty()){
            throw new MemberBusinessException(
                    MemberResultEnum.MEMBER_NOT_FOUND
            );
        }
        List<GroupMember> groupMemberListR = new ArrayList<>();
        for(Group group:groupList){
          List<GroupMember> groupMemberList = groupMemberMapper.selectGroupMembers(group.getGid());
          if(groupMemberList.isEmpty()){
              continue;
          }
          groupMemberListR.addAll(groupMemberList);
        }
        return ResponseEntity.ok(
                new Result<>(MemberResultEnum.MEMBER_GET_SUCCESS,
                        groupMemberListR
                )
        );
    }
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Result<?>> updateGroupMember(GroupMember groupMember, MultipartFile file) throws IOException {

//            byte[] feature = ssdcaffem.DnnSSDcaffemDetectionFeature(file.getBytes().length,file.getBytes());
            byte[] feature = yunet.DnnYuNetDebugTime(file.getBytes().length,file.getBytes());
            if(feature.length==0){
                throw new MemberBusinessException(
                        MemberResultEnum.MEMBER_DETECT_NOT_FACE
                );
            }
            groupMember.setMemberFeature(feature);
            groupMember.setUpdateTime(LocalDateTime.now());
            log.info("更新成员信息:{}",groupMember);
            return ResponseEntity.ok(
                    new Result<>(MemberResultEnum.MEMBER_UPDATE_SUCCESS,
                            groupMemberMapper.updateGroupMember(groupMember)
                    )
            );
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Result<?>> updateGroupMemberNoFeatures(GroupMember groupMember){
        groupMember.setUpdateTime(LocalDateTime.now());
        return ResponseEntity.ok(
                new Result<>(MemberResultEnum.MEMBER_UPDATE_SUCCESS,
                            groupMemberMapper.updateGroupMemberNoFeatures(groupMember)
                )
        );
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Result<?>> deleteGroupMember(int gid,int mid){
            boolean isDelete=groupMemberMapper.deleteGroupMember(gid,mid);
            if(!isDelete){
                throw new MemberBusinessException(
                        MemberResultEnum.MEMBER_NOT_FOUND
                );
            }
            return ResponseEntity.ok(
                    new Result<>(
                             MemberResultEnum.MEMBER_DELETE_SUCCESS,
                            true
                    )
            );

    }

    public void MakeTest(MultipartFile file) throws IOException {
            byte[] feature = yunet.DnnYuNetDebugTime(file.getBytes().length,file.getBytes());

    }



}
