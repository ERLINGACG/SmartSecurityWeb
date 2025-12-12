package com.erling.service.detectHistroy;

import com.erling.dao.detect.DetectionHistoryMapper;
import com.erling.entity.detect.DetectionHistory;
import com.erling.utils.log.Logger;
import com.erling.utils.result.ren.DetectionHistoryResultEnum;
import com.erling.utils.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Service
public class DetectionHistoryService {

    private final DetectionHistoryMapper detectionHistoryMapper;
    @Autowired
    public DetectionHistoryService(DetectionHistoryMapper detectionHistoryMapper) {
        this.detectionHistoryMapper = detectionHistoryMapper;
    }

    public boolean insert(DetectionHistory detectionHistory){
      return  detectionHistoryMapper.insert(detectionHistory);
    }

    public ResponseEntity<Result<?>> getDetectionHistoryByTopic(String topic) {
        return ResponseEntity.
                ok(new Result<>(
                            DetectionHistoryResultEnum.DETECTION_HISTORY_SUCCESS,
                            detectionHistoryMapper.findByTopic(topic)
                        )
                );

    }

    public ResponseEntity<Result<?>> getDetectionHistoryByDateBetween(
            String topic,
            String startDate,
            String endDate
    ) {
        List<DetectionHistory> detectionHistory = detectionHistoryMapper.findByTopicAndDateBetween(
                topic,
                endDate,
                startDate
        );
        return ResponseEntity.
                    ok(new Result<>(
                            DetectionHistoryResultEnum.DETECTION_HISTORY_SUCCESS,
                            detectionHistory
                    )
        );

    }

    public ResponseEntity<byte[]> getDetectionHistoryByPath(String topic, String path) {
        DetectionHistory detectionHistory = detectionHistoryMapper.findByTopicAndPath(topic, path);
        if (detectionHistory == null) {
            return ResponseEntity.ok(null);
        }
        String path_ = detectionHistory.getPath();
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(path_));
            return ResponseEntity.
                    status(HttpStatus.OK).
                    contentType(MediaType.IMAGE_JPEG).
                    body(bytes);
        } catch (Exception e) {
            Logger.getLogger(DetectionHistoryService.class).error("读取文件失败");
            return ResponseEntity.ok(null);
        }
    }
}
