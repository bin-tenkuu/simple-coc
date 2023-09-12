import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

group = "com.starhz"
version = "1.0.0"

dependencies {
    // spring
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-json")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    implementation("org.springframework.boot:spring-boot-starter-security")
    // lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    compileOnly("org.jetbrains:annotations:24.0.1")
    // sql
    implementation("org.postgresql:postgresql:42.5.4")
    implementation("org.xerial:sqlite-jdbc:3.42.0.0")
    implementation("com.baomidou:mybatis-plus-boot-starter:3.5.3.1")
    implementation("com.baomidou:dynamic-datasource-spring-boot-starter:3.5.1")
    // swagger
    implementation("com.github.xiaoymin:knife4j-openapi3-jakarta-spring-boot-starter:4.1.0")
    // hutool全家桶: https://hutool.cn/docs/#/
    implementation("cn.hutool:hutool-core:5.8.16")
    // minio
    implementation("io.minio:minio:8.5.2")
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

tasks {
    withType<BootJar> {
        archiveBaseName.set("server")
        archiveAppendix.set("exec")
        archiveVersion.set("")
    }
}
