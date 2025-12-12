package com.erling.lib.opencv.dnn;

import com.erling.lib.opencv.struct.output.ImageData;
import com.erling.lib.opencv.struct.output.OutputJson;
import com.sun.jna.Library;
import com.sun.jna.Pointer;

public interface DnnYoloV5 extends Library {
   Pointer DnnYoloV5Create(String configPath);
   void DnnYoloV5Destroy(Pointer ptr);
   void DnnYoloV5Debug(Pointer ptr);

   void DnnYoloV5SetSR(Pointer ptr, Pointer sr);

   void DnnYoloV5DebugTime(Pointer ptr, int size, byte[] bytes, ImageData output,
                           OutputJson outputJson);
   void DnnYoloV5Detection(Pointer ptr, int size, byte[] bytes, ImageData output,
                           OutputJson outputJson);

   void DnnYoloV5DebugSRTime(Pointer ptr, int size, byte[] bytes, ImageData output,
                             OutputJson outputJson);

}
