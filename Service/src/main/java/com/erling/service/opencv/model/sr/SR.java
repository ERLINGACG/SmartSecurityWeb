package com.erling.service.opencv.model.sr;

import com.erling.lib.ann.DyLibrary;
import com.erling.lib.load.Loader;
import com.erling.lib.opencv.dnn.DnnSR;
import com.erling.utils.tomls.ReadToml;
import com.sun.jna.Pointer;
import lombok.Getter;

public class SR {
    @DyLibrary(TomlPath = "/config/Lib.toml")
    interface IDnnSR extends DnnSR {};
    IDnnSR IDnnSR;

    @Getter
    final Pointer netPtr;
    public SR(){
        IDnnSR = Loader.LoadTLibrary(IDnnSR.class);
        netPtr=IDnnSR.DnnSRCreate(
                ReadToml.TomlString("/config/Lib.toml","SRPath"));
    }

}
