package com.erling.lib.opencv.struct.output;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

import java.nio.charset.StandardCharsets;


/**
 * C++端定义
 * struct OutputJson{
 *         std::unique_ptr<char[]> json;
 *         int size;
 *     };
 */
@Structure.FieldOrder({"json", "size"})
public class OutputJson extends Structure {
    public Pointer json;
    public int size;

    public String getDataJsonUtf8(){
        if( size == 0){
            return "";
        }else{
            return new String(json.getByteArray(0, size), StandardCharsets.UTF_8);
        }

    }

}
