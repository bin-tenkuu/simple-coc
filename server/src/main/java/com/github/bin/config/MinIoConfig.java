//package com.github.bin.config;
//
//import io.minio.MinioClient;
//import lombok.Getter;
//import lombok.Setter;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.stereotype.Component;
//
///**
// * @author bin
// * @since 2023/08/24
// */
//@Getter
//@Setter
//@Component
//@ConfigurationProperties(prefix = "minio")
//public class MinIoConfig {
//    private String endpoint;
//    private String accessKey;
//    private String secretKey;
//
//    public MinioClient getClient() {
//        return MinioClient.builder()
//                .endpoint(endpoint)
//                .credentials(accessKey, secretKey)
//                .build();
//    }
//}
