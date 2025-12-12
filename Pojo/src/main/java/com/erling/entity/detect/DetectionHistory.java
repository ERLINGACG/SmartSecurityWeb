package com.erling.entity.detect;

import lombok.*;

import java.time.LocalDateTime;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
public class DetectionHistory {
    int id;
    String topic;
    String path;
    LocalDateTime date;

    public DetectionHistory _setTopic(String topic){
        setTopic(topic);
        return this;
    }
    public DetectionHistory _setPath(String path){
        setPath(path);
        return this;
    }
    public DetectionHistory _setDate(LocalDateTime date){
        setDate(date);
        return this;
    }

}
