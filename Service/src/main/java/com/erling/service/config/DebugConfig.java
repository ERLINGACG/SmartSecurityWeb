package com.erling.service.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class DebugConfig {
    @Value("${Idebug.islog}")
    private boolean isLog;

    @Value("${Idebug.showDebug}")
    private boolean showDebug;



}
