package com.github.bin.service;

import com.github.bin.config.MinIoConfig;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.errors.MinioException;
import lombok.val;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * @author bin
 * @since 2023/08/22
 */
@Service
public class FileService {
    private static final String BUCKET = "chat";
    private final MinioClient minioClient;

    public FileService(MinIoConfig config) {
        minioClient = config.getClient();
    }

    public ResponseEntity<InputStreamResource> downloadFile(String filePath, HttpHeaders headers, boolean download)
            throws MinioException, IOException, GeneralSecurityException {
        val statObject = minioClient.statObject(StatObjectArgs.builder()
                .bucket(BUCKET)
                .object(filePath)
                .build());
        val getObject = minioClient.getObject(GetObjectArgs.builder()
                .bucket(BUCKET)
                .object(filePath)
                .build());
        val index = filePath.lastIndexOf('/');
        val filename = (index >= 0) ? filePath.substring(index) : filePath;
        val respHeaders = new HttpHeaders();
        respHeaders.setContentLength(statObject.size());
        if (download) {
            respHeaders.setContentDisposition(ContentDisposition.attachment()
                    .filename(filename)
                    .build());
            respHeaders.setContentType(MediaType.valueOf(statObject.contentType()));
        } else {
            respHeaders.setContentDisposition(ContentDisposition.inline()
                    .filename(filename)
                    .build());
            respHeaders.setContentType(headers.getAccept().getFirst());
        }
        respHeaders.setLastModified(statObject.lastModified());
        return ResponseEntity.ok()
                .headers(respHeaders)
                .body(new InputStreamResource(getObject));
    }

    public String uploadFile(String filePath, MultipartFile mutipartFile)
            throws MinioException, IOException, GeneralSecurityException {
        try (val it = mutipartFile.getInputStream()) {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(BUCKET)
                    .object(filePath)
                    .stream(it, mutipartFile.getSize(), -1)
                    .contentType(mutipartFile.getContentType())
                    .build());
        }
        return filePath;
    }

}
