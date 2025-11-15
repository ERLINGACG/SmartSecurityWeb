package com.erling.service.group.dlib;

import com.erling.lib.dlib.face.FacialRecognition;
import com.erling.lib.ann.LibraryAnn;
import com.erling.lib.instance.PathConfig;

@LibraryAnn(
        WindowsPath = PathConfig.WindowsPath,
        LinuxPath = PathConfig.LinuxPath
)
public interface FacialRecognitionE extends FacialRecognition {}
