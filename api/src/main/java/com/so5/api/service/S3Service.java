package com.so5.api.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.so5.api.config.properties.AwsProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final AwsProperties awsProperties;

    private final AmazonS3 amazonS3;

    public String upload(ByteArrayOutputStream os, String sku, String contentType) {

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(os.toByteArray().length);
        metadata.setContentType(contentType);

        InputStream is = new ByteArrayInputStream(os.toByteArray());

        amazonS3.putObject(awsProperties.getBucket(), sku, is, metadata);

        return awsProperties.getServer().getHost() + "/" + awsProperties.getBucket() + "/" + sku;
    }
}
