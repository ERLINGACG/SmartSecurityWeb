package com.erling.lib.load;

import com.erling.lib.ann.DyLibrary;
import com.erling.utils.tomls.ReadToml;
import com.sun.jna.Library;
import com.sun.jna.Native;

public class Loader {

    @SuppressWarnings("unchecked")
    public static <T extends Library> T LoadLibrary(Class<? extends Library> libraryClass){
            DyLibrary annotation = libraryClass.getAnnotation(DyLibrary.class);
            if (annotation != null && annotation.DynamicPath()!= null  ) {
                    String path = annotation.DynamicPath();
                    if(System.getProperty("os.name").toLowerCase().contains("win")){
                          path+=".dll";
                    }else{
                          path+=".so";
                    }
                    return (T) Native.load(
                            (path),
                            libraryClass
                    );
            }else{
                throw  new RuntimeException("DyLibrary annotation is null");
            }
    }
    @SuppressWarnings("unchecked")
    public static <T extends Library> T LoadTLibrary(Class<? extends Library> libraryClass){
        DyLibrary annotation = libraryClass.getAnnotation(DyLibrary.class);
        if (annotation!=null && annotation.TomlPath()!= null) {
                String path = ReadToml.TomlString(annotation.TomlPath(),"LibPath");
                if(System.getProperty("os.name").toLowerCase().contains("win")){
                      path+=".dll";
                }else{
                      path+=".so";
                }
                return (T) Native.load(
                        (path),
                        libraryClass
                );
        }else{
            throw  new RuntimeException("DyLibrary annotation is null");
        }
    }




}
