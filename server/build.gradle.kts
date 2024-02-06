import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

group = "com.github.bin"
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
//    implementation("org.springframework.boot:spring-boot-starter-aop")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
//    implementation("org.hibernate.orm:hibernate-community-dialects")
    // lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok:1.18.30")
    compileOnly("org.jetbrains:annotations:24.0.1")
    // sql
    implementation("org.postgresql:postgresql:42.5.4")
    implementation("org.xerial:sqlite-jdbc:3.45.1.0")
//    implementation("com.baomidou:mybatis-plus-boot-starter:3.5.5")
//    implementation("com.baomidou:dynamic-datasource-spring-boot-starter:3.5.1")
    // swagger
    implementation("com.github.xiaoymin:knife4j-openapi3-jakarta-spring-boot-starter:4.3.0")
    // hutool全家桶: https://hutool.cn/docs/#/
//    implementation("cn.hutool:hutool-core:5.8.25")
    // org.mapstruct
    compileOnly("org.mapstruct:mapstruct")
    annotationProcessor("org.mapstruct:mapstruct:1.5.5.Final")
    // minio
//    implementation("io.minio:minio:8.5.2")
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

tasks {
    create("copyLib", Copy::class) {
        group = "build"
        into(projectDir.resolve("build/libs/lib"))
        from(configurations.runtimeClasspath)
    }
    withType<BootJar> {
        archiveBaseName.set("server")
        archiveAppendix.set("exec")
        archiveVersion.set("")
        exclude("*.jar")
        mainClass = "com.github.bin.ApplicationStarter"
//        dependsOn("copyLib")
        manifest {
            attributes["Class-Path"] = configurations.runtimeClasspath.get().joinToString(" ") { "lib/" + it.name }
//            attributes["Class-Path"] = "./lib.jar"
        }
    }
}

