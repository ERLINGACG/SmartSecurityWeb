package com.erling.dao.user;

import com.erling.entity.user.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {
  @Insert("INSERT INTO user (email, password_hash,created_at) VALUES (#{email}, #{passwordHash}, #{createdAt})")
  boolean insertUser(User user);

  @Update("UPDATE user SET avatarPath = #{avatarPath} WHERE uid = #{uid}")
  boolean addAvatar(User user);
  @Delete("DELETE FROM user WHERE email = #{email}")
  boolean deleteUserByEmail(String email);

  @Update("UPDATE user SET nickName = #{nickName} WHERE email = #{email}")
  boolean updateNickname(String nickName, String email);

  @Select("SELECT * FROM user WHERE email = #{email}")
  User getUserByEmail(String email);

  @Select("SELECT user.uid," +
                  "user.email," +
                  "user.nickName," +
                  "user.created_at," +
                  "user.avatarPath FROM user WHERE user.email = #{email}")
  User getUserByEmailNotPwd(String email);

  @Select("SELECT user.avatarPath FROM user WHERE uid = #{uid}")
  String getUserAvatarByUid(int uid);

  @Select("SELECT * FROM user")  //测试接口
  List<User> getAllUsers();
}
