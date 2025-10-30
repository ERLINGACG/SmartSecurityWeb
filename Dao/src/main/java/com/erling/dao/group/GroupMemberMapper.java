package com.erling.dao.group;

import com.erling.entity.group.GroupMember;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface GroupMemberMapper {

    @Insert("INSERT INTO smartsecuritydb.groupmember (" +
            "groupId,memberEmail," +
            "memberName,memberIdentity,memberGender,memberDescription,memberFeature,updateTime) " +
            "VALUES (#{groupId}, " +
            "#{memberEmail}," +
            "#{memberName},#{memberIdentity},#{memberGender},#{memberDescription},#{memberFeature},#{updateTime})")
    boolean insertGroupMember(GroupMember groupMember);


    @Delete("DELETE FROM smartsecuritydb.groupmember WHERE groupId=#{groupId} AND mid=#{mid}")
    boolean deleteGroupMember(int groupId,int mid);


    @Update("UPDATE smartsecuritydb.groupmember SET " +
            "memberName=#{memberName}, memberIdentity=#{memberIdentity}, " +
            "memberFeature=#{memberFeature}, memberDescription=#{memberDescription},updateTime=#{updateTime} " +
            "WHERE groupId=#{groupId} AND mid=#{mid}")
    boolean updateGroupMember(GroupMember groupMember);

    @Update("UPDATE smartsecuritydb.groupmember SET " +
            "memberName=#{memberName}, memberIdentity=#{memberIdentity}, " +
            "memberDescription=#{memberDescription},updateTime=#{updateTime} " +
            "WHERE groupId=#{groupId} AND mid=#{mid}")
    boolean updateGroupMemberNoFeatures(GroupMember groupMember);

    @Select("SELECT mid,groupId,memberName,memberEmail,memberGender,memberIdentity,memberDescription,updateTime " +
            "FROM smartsecuritydb.groupmember " +
            "where groupmember.groupId=#{gid}")
    List<GroupMember> selectGroupMembers(int gid);



    @Select("SELECT * FROM smartsecuritydb.groupmember where groupmember.groupId=#{gid}")
    List<GroupMember> selectGroupMembersALL(int gid);
    @Select("SELECT * FROM smartsecuritydb.groupmember where groupmember.groupId=#{gid} AND groupmember.memberEmail=#{email}")
    GroupMember selectGroupMember(int gid,String email);





}