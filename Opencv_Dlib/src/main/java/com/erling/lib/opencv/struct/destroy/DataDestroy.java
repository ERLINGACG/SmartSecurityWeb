package com.erling.lib.opencv.struct.destroy;

import com.erling.lib.opencv.struct.output.FaceFeatureByte;
import com.erling.lib.opencv.struct.output.ImageData;
import com.erling.lib.opencv.struct.output.OutputJson;
import com.sun.jna.Library;





public interface DataDestroy extends Library {
    /**
     *  EXPORT_USE CORE_CV_API void ImageDataDestroy  (const ImageData* i)      { delete i; }
     */
    void ImageDataDestroy(ImageData i);
    /**
     * EXPORT_USE CORE_CV_API void FaceFeatureDestroy(const FaceFeatureByte* i){ delete i; }
     */
    void FaceFeatureDestroy(FaceFeatureByte i);

    /**
     *EXPORT_USE CORE_CV_API void OutputJsonDestroy (const OutputJson* o)     { delete o; }
     */
    void OutputJsonDestroy(OutputJson o);





}
