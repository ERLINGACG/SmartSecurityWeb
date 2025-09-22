package com.erling.lib.instance;

import com.sun.jna.Library;
import com.sun.jna.Native;


public class Load {
    private final Class<? extends Library> targetInterface;

    public Load(Class<? extends Library> targetInterface) {
        this.targetInterface = targetInterface;
    }
    public  <T extends Library> T loading() {
        return getT(targetInterface);
    }
    public static <T extends Library> T loading(Class<T> targetInterface){
        return getT(targetInterface);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Library> T getT(Class<? extends Library> targetInterface) {
        try{
            LibraryAnn annotation = targetInterface.getAnnotation(LibraryAnn.class);
            if(System.getProperty("os.name").toLowerCase().contains("win")){
                if (annotation != null && annotation.WindowsPath()!= null  ) {
                    return (T) Native.load(
                            (annotation.WindowsPath()+".dll"),
                            targetInterface
                    );
                }else{
                    return null;
                }
            }else {
                if (annotation != null && annotation.WindowsPath()!= null  ) {
                    return (T) Native.load(
                           annotation.LinuxPath()+".so",
                            targetInterface
                    );
                }else{
                    return null;
                }
            }
//
        }catch(Throwable e){
            System.out.println(e.getMessage());
            return null;
        }
    }
}
