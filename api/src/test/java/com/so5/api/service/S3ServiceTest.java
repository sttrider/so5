package com.so5.api.service;

import com.so5.api.config.properties.AwsProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.ByteArrayOutputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class S3ServiceTest {

    @Mock
    private AwsProperties awsProperties;

    @Mock
    private S3Client s3Client;

    @Captor
    ArgumentCaptor<DeleteObjectRequest> deleteObjectRequestCaptor;

    @InjectMocks
    private S3Service s3Service;

    @Test
    public void whenUpload_thenShouldCallS3Client() {

        var sku = "sku1";
        var bucket = "so5";

        when(awsProperties.getBucket()).thenReturn(bucket);

        var url = s3Service.upload(new ByteArrayOutputStream(), sku, "contentType");

        verify(s3Client).putObject(Mockito.any(PutObjectRequest.class), Mockito.any(RequestBody.class));

        assertThat(url).isEqualTo("http://localhost:4566/" + bucket + "/" + sku);
    }

    @Test
    public void whenDelete_thenShouldCallS3Client() {

        var sku = "sku1";
        var bucket = "so5";

        when(awsProperties.getBucket()).thenReturn(bucket);

        s3Service.delete(sku);

        verify(s3Client).deleteObject(deleteObjectRequestCaptor.capture());

        var captured = deleteObjectRequestCaptor.getValue();

        assertThat(captured.bucket()).isEqualTo(bucket);
        assertThat(captured.key()).isEqualTo(sku);
    }
}
