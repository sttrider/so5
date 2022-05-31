package com.so5.api.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "imagemagick")
public class ImageMagickProperties {

    private String path;
    private Integer resize;
}
