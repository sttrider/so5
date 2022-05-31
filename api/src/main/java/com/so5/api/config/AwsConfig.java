package com.so5.api.config;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.so5.api.config.properties.AwsProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class AwsConfig {

    private final AwsProperties awsProperties;

    @Bean
    public AmazonS3 amazonS3() {
        var client = AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(awsProperties.getServer().getHost(), awsProperties.getServer().getRegion())).build();
        if (!client.doesBucketExistV2(awsProperties.getBucket())) {
            client.createBucket(awsProperties.getBucket());
        }
        return client;
    }
}
