package com.erling.service.tcpservice.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class TcpConfig {
    @Getter
    @Value("${tcp.service.port}")
    private int port;

    @Getter
    @Value("${tcp.connection.threads}")
    private int connectionThreads;

    @Getter
    @Value("${tcp.connection.timeout}")
    private int connectionTimeout;

    @Getter
    @Value("${tcp.connection.connKey}")
    private String connKey;
}
