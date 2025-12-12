package com.erling.dao.group;

import com.erling.entity.group.Group;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface GroupMapper {

    @Select("SELECT * FROM smartsecuritydb.`group` WHERE `group`.groupName = #{groupName} AND `group`.groupEmail = #{email}")
    Group getGroup(String groupName,String email);

    @Select("SELECT * FROM smartsecuritydb.`group` WHERE `group`.gid = #{gid}")
    Group getGroupById(int gid);

    @Select("SELECT * FROM smartsecuritydb.`group` WHERE `group`.groupEmail = #{email}")
    List<Group> getGroups(String email);

    @Select("SELECT * FROM smartsecuritydb.`group` WHERE `group`.groupTopic=#{groupTopic}")
    List<Group> getGroupsByTopic(String groupTopic);



    @Insert("INSERT INTO smartsecuritydb.`group` " +
            "(groupName,groupEmail,groupTopic,groupDescription,groupVisibility) " +
            "VALUES (#{groupName},#{groupEmail}, #{groupTopic}, #{groupDescription},#{groupVisibility})")
    boolean insertGroup(Group group);


    @Update("UPDATE smartsecuritydb.`group` " +
            "SET groupName=#{groupName}," +
            "groupTopic=#{groupTopic}," +
            "groupDescription=#{groupDescription}," +
            "groupVisibility=#{groupVisibility} " +
            "WHERE gid=#{gid} AND groupEmail=#{groupEmail}")
    boolean updateGroup(Group group);

    @Delete("DELETE FROM smartsecuritydb.`group` WHERE gid=#{gid} AND groupEmail=#{email}")
    boolean deleteGroup(int gid,String email);
}
