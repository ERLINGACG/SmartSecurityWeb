package com.erling;

import com.erling.env.OpenCVDllLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
    Application(){
        OpenCVDllLoader.INSTANCE.LOAD();
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
