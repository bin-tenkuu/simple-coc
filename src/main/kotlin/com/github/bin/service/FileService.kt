package com.github.bin.service

import io.minio.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.InputStreamResource
import org.springframework.http.ContentDisposition
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.nio.charset.StandardCharsets


/**
 * @author bin
 * @since 2023/07/11
 */
@Service
class FileService(
        @Value("\${minio.endpoint}")
        endpoint: String,
        @Value("\${minio.accessKey}")
        accessKey: String,
        @Value("\${minio.secretKey}")
        secretKey: String,
        @Value("\${minio.bucket}")
        private val bucket: String,
) {
    private val minioClient: MinioClient = MinioClient.builder()
            .endpoint(endpoint)
            .credentials(accessKey, secretKey)
            .build()

    fun downloadFile(filePath: String): ResponseEntity<InputStreamResource> {
        val statObject: StatObjectResponse = minioClient.statObject(StatObjectArgs.builder()
                .bucket(bucket)
                .`object`(filePath)
                .build())
        val getObject: GetObjectResponse = minioClient.getObject(GetObjectArgs.builder()
                .bucket(bucket)
                .`object`(filePath)
                .build())
        val index: Int = filePath.lastIndexOf('/')
        val filename = if (index >= 0) filePath.substring(index) else filePath
        val headers = HttpHeaders()
        headers.eTag = statObject.etag()
        headers.contentLength = statObject.size()
        headers.contentDisposition = ContentDisposition.attachment()
                .filename(filename, StandardCharsets.UTF_8)
                .build()
        headers.setLastModified(statObject.lastModified())
//        headers.contentType = MediaType.APPLICATION_OCTET_STREAM
        headers.contentType = MediaType.valueOf(statObject.contentType())
        return ResponseEntity.ok()
                .headers(headers)
                .body(InputStreamResource(getObject))
    }

    fun uploadFile(filePath: String, mutipartFile: MultipartFile): String {
        mutipartFile.inputStream.use {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucket)
                    .`object`(filePath)
                    .stream(it, mutipartFile.size, -1)
                    .contentType(mutipartFile.contentType)
                    .build())
        }
        return filePath
    }

    fun deleteFile(filePath: String) {
        minioClient.removeObject(RemoveObjectArgs.builder()
                .bucket(bucket)
                .`object`(filePath)
                .build())
    }
}
