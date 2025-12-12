package com.erling.dao.detect;

import com.erling.entity.detect.DetectionHistory;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DetectionHistoryMapper {
    @Select("SELECT * FROM smartsecuritydb.detectionhistory WHERE topic = #{topic}")
    List<DetectionHistory> findByTopic(String topic);


    @Select("SELECT * FROM smartsecuritydb.detectionhistory WHERE  topic = #{topic} AND path = #{path}")
    DetectionHistory findByTopicAndPath(String topic, String path);


    @Select("SELECT * FROM smartsecuritydb.detectionhistory WHERE  topic = #{topic} AND date BETWEEN #{startDate} AND #{endDate}")
    List<DetectionHistory> findByTopicAndDateBetween(String topic, String startDate, String endDate);

    @Insert("INSERT INTO smartsecuritydb.detectionhistory (topic,date,path) VALUES (#{topic}, #{date}, #{path})")
    boolean insert(DetectionHistory detectionHistory);






}
