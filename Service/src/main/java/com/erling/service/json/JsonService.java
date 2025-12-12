package com.erling.service.json;

import com.erling.lib.ann.LibraryAnn;
import com.erling.lib.instance.Load;
import com.erling.lib.instance.PathConfig;
import com.erling.lib.java.NlohmannJava;
import com.erling.lib.opencv.struct.destroy.DataDestroy;
import com.erling.lib.opencv.struct.output.OutputJson;
import com.erling.service.obj.ServiceObject;
import org.springframework.stereotype.Service;

@Service
public class JsonService extends ServiceObject {
    @LibraryAnn(
            WindowsPath = PathConfig.WindowsPath
    )
    interface Cjson extends NlohmannJava {}

    @LibraryAnn(
            WindowsPath = PathConfig.WindowsPath
    )
    interface dataDestroy extends DataDestroy {}

    Cjson cjson;
    dataDestroy dataDestroy;
    JsonService(){
        this.cjson= Load.loading(Cjson.class);
        this.dataDestroy= Load.loading(dataDestroy.class);
    }


    public String ToJsonOutput(String json){
        OutputJson outputJson=new OutputJson();
        cjson.ToJsonOutput(json,outputJson);
        return outputJson.getDataJsonUtf8();

    }

    public void ToJsonTest(String json){
            cjson.ToJsonTest(json,new OutputJson());
    }
}
