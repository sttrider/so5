package com.so5.api.config;

import com.so5.api.config.properties.AwsProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;

import java.net.URI;
import java.net.URISyntaxException;

@Configuration
@RequiredArgsConstructor
public class AwsConfig {

    private final AwsProperties awsProperties;

    @Bean
    public S3Client s3Client() throws URISyntaxException {
        var s3 = S3Client.builder().endpointOverride(new URI(awsProperties.getServer().getHost())).region(Region.US_EAST_1).credentialsProvider(() -> new AwsCredentials() {
            @Override
            public String accessKeyId() {
                return "123";
            }

            @Override
            public String secretAccessKey() {
                return "xyz";
            }
        }).build();
        s3.createBucket(CreateBucketRequest.builder().bucket(awsProperties.getBucket()).build());
        return s3;
    }
}
