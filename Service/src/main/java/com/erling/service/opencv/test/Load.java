package com.erling.service.opencv.test;

import com.erling.service.opencv.model.facenet.ArcFace;
import com.erling.service.opencv.model.yunet.YuNet;
import org.springframework.stereotype.Service;

@Service
public class Load {
    Load(){
        YuNet yuNet =new YuNet(new ArcFace());
    }
}
