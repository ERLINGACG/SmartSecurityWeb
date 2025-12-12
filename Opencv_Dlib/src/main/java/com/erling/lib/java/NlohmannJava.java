package com.erling.lib.java;

import com.erling.lib.opencv.struct.output.OutputJson;
import com.sun.jna.Library;

public interface NlohmannJava extends Library {
    void ToJson(String json);
    void ToJsonOutput(String json, OutputJson output);

    void ToJsonTest(String json, OutputJson output);
}
