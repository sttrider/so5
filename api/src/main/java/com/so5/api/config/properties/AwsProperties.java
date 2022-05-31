package com.so5.api.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "aws")
public class AwsProperties {

    private String bucket;
    private Server server;

    @Data
    public static class Server {

        private String host;
        private String region;
    }
}
