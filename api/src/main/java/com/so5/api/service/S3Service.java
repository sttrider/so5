package com.so5.api.service;

import com.so5.api.config.properties.AwsProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.ByteArrayOutputStream;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final AwsProperties awsProperties;

    private final S3Client s3Client;

    public String upload(ByteArrayOutputStream os, String sku, String contentType) {

        var bytes = os.toByteArray();
        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(awsProperties.getBucket())
                .key(sku)
                .contentType(contentType)
                .contentLength((long) bytes.length)
                .build();

        s3Client.putObject(objectRequest, RequestBody.fromBytes(bytes));

        return "http://localhost:4566/" + awsProperties.getBucket() + "/" + sku;
    }
}
