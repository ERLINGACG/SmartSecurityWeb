package com.erling.env;

import com.erling.utils.tomls.ReadToml;

public enum OpenCVDllLoader {
    INSTANCE;

    OpenCVDllLoader() {
        try {
            // 直接写 DLL 文件的全路径（注意用双反斜杠或单斜杠）
            String dllFullPath = ReadToml.TomlString("/config/Lib.toml","ENV");
            System.load(dllFullPath); // 加载指定路径的 DLL
            System.out.println("DLL 加载成功！" + dllFullPath);
//             System.out.println("DLL 加载成功！" + "/mnt/e/SmartSecurity/testRun/lib/x64/debug/libSmartSecurityCoreLibTest.so");
        } catch (UnsatisfiedLinkError e) {
            throw new RuntimeException(e);
        }
    }
    public void LOAD(){}
}
