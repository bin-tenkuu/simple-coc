package com.github.bin.service;

import io.minio.*;
import io.minio.errors.MinioException;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;

/**
 * @author bin
 * @since 2023/08/22
 */
@Service
public class FileService {
    private final String bucket;
    private final MinioClient minioClient;

    public FileService(
            @Value("${minio.endpoint}")
            String endpoint,
            @Value("${minio.accessKey}")
            String accessKey,
            @Value("${minio.secretKey}")
            String secretKey,
            @Value("${minio.bucket}")
            String bucket
    ) {
        minioClient = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
        this.bucket = bucket;
    }


    public ResponseEntity<InputStreamResource> downloadFile(String filePath)
            throws MinioException, IOException, GeneralSecurityException {
        val statObject = minioClient.statObject(StatObjectArgs.builder()
                .bucket(bucket)
                .object(filePath)
                .build());
        val getObject = minioClient.getObject(GetObjectArgs.builder()
                .bucket(bucket)
                .object(filePath)
                .build());
        val index = filePath.lastIndexOf('/');
        val filename = (index >= 0) ? filePath.substring(index) : filePath;
        val headers = new HttpHeaders();
        headers.setETag(statObject.etag());
        headers.setContentLength(statObject.size());
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename(filename, StandardCharsets.UTF_8)
                .build());
        headers.setLastModified(statObject.lastModified());
//        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentType(MediaType.valueOf(statObject.contentType()));
        return ResponseEntity.ok()
                .headers(headers)
                .body(new InputStreamResource(getObject));
    }

    public String uploadFile(String filePath, MultipartFile mutipartFile)
            throws MinioException, IOException, GeneralSecurityException {
        try (val it = mutipartFile.getInputStream()) {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(filePath)
                    .stream(it, mutipartFile.getSize(), -1)
                    .contentType(mutipartFile.getContentType())
                    .build());
        }
        return filePath;
    }

    public void deleteFile(String filePath)
            throws MinioException, IOException, GeneralSecurityException {
        minioClient.removeObject(RemoveObjectArgs.builder()
                .bucket(bucket)
                .object(filePath)
                .build());
    }
}
