package com.so5.api.config;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsConfig {

    public static final String BUCKET_NAME = "so5";

    @Bean
    public AmazonS3 amazonS3() {
        var client = AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://s3.localhost.localstack.cloud:4566", "us-east-1")).build();
        if (!client.doesBucketExistV2(BUCKET_NAME)) {
            client.createBucket(BUCKET_NAME);
        }
        return client;
    }
}
