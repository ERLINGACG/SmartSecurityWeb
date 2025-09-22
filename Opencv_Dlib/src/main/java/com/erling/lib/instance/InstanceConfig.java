package com.erling.lib.instance;


import lombok.Getter;

@Getter
public enum InstanceConfig {
    OPENCV_4120_RELEASE(
            "lib/x64/debug/SmartSecurityCoreLibTest",
            "/mnt/e/ExamplePLAN/RUN/java1/lib/x64/debug/SmartSecurityCoreLibTest"),;

    private final String WindowsPath;
    private final String LinuxPath;


    InstanceConfig(String WindowsPath,String LinuxPath) {
        this.WindowsPath = WindowsPath+".dll";
        this.LinuxPath = LinuxPath+".so";

//        if (!path.endsWith(extension)) {
//            this.path = path + extension;
//        } else {
//            this.path = path;
//        }
    }
}
