package com.erling.service.group;

import com.erling.dao.group.GroupMapper;
import com.erling.dao.group.GroupMemberMapper;
import com.erling.entity.group.Group;
import com.erling.entity.group.GroupMember;
import com.erling.service.exception.exc.GroupBusinessException;
import com.erling.service.obj.ServiceObject;
import com.erling.utils.result.Result;
import com.erling.utils.result.ResultEnum;
import com.erling.utils.result.ren.GroupResultEnum;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import java.util.List;

@Service
public class GroupService extends ServiceObject {
    GroupMapper  groupMapper;
    GroupMemberMapper groupMemberMapper;
    public GroupService(GroupMapper groupMapper,GroupMemberMapper groupMemberMapper) {
        this.groupMapper = groupMapper;
        this.groupMemberMapper = groupMemberMapper;
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Result<?>> addGroup(Group group, BindingResult result) {
        validate(result);
        return ResponseEntity.
                    ok(new Result<>
                            (ResultEnum.GROUP_ADD_SUCCESS,
                                    groupMapper.insertGroup(group)
                            )
                    );
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Result<?>> deleteGroup(int groupId,String groupEmail) {

         List<GroupMember> groupMembers = groupMemberMapper.selectGroupMembers(groupId);
         for(GroupMember groupMember : groupMembers){
             Boolean flag =  groupMemberMapper.deleteGroupMember(groupId,groupMember.getMid());
             this.log.debug("删除成员：{}",groupMember,flag);
         }
         if(groupMapper.deleteGroup(groupId,groupEmail)){
                return ResponseEntity.
                        ok(new Result<>(
                                GroupResultEnum.DELETE_GROUP_SUCCESS,true
                            )
                        );
         }else{
                 throw new GroupBusinessException(GroupResultEnum.GROUP_NOT_EXIST);
         }
    }
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<Result<?>> updateGroup(Group group, BindingResult result) {
        validate(result);
        if(groupMapper.getGroupById(group.getGid()) != null){
            return ResponseEntity.
                    ok(new Result<>(
                            GroupResultEnum.UPDATE_GROUP_SUCCESS,
                            groupMapper.updateGroup(group)
                        )
                    );
        }else{
           throw new GroupBusinessException(GroupResultEnum.GROUP_NOT_EXIST);
        }
    }

    public ResponseEntity<Result<?>> getGroup(String groupName,String groupEmail) {
        Group group = groupMapper.getGroup(groupName,groupEmail);
        if(group != null){
            return ResponseEntity.
                        ok(new Result<>(
                                GroupResultEnum.SELECT_GROUP_SUCCESS,
                                group
                           )
                        );
        }else{
            throw new GroupBusinessException(GroupResultEnum.GROUP_NOT_EXIST);
        }

    }
    public ResponseEntity<Result<?>> getGroups(String groupEmail) {
            return ResponseEntity.
                    ok(new Result<>(
                                ResultEnum.GROUP_SELECT_SUCCESS,
                                groupMapper.getGroups(groupEmail)
                       )
                    );

    }
     public ResponseEntity<Result<?>> getGroupsByTopic(String groupTopic) {
            return ResponseEntity.
                    ok(new Result<>(
                                ResultEnum.GROUP_SELECT_SUCCESS,
                                groupMapper.getGroupsByTopic(groupTopic)
                       )
                    );
     }
}
