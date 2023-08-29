//val kotlinVersion = "1.9.0"
plugins {
//    val kotlinVersion = "1.9.0"
    java
    application
//    kotlin("jvm") version kotlinVersion
//    kotlin("plugin.spring") version kotlinVersion
    id("org.springframework.boot") version "3.0.5"
    id("io.spring.dependency-management") version "1.1.0"
}

group = "com.github.bin"
version = "1.0.0"

repositories {
    maven("https://maven.aliyun.com/repository/public") // 阿里云国内代理仓库
    mavenLocal()
    mavenCentral()
}

dependencies {
    // spring
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    // lombok
    compileOnly("org.projectlombok:lombok:1.18.26")
    annotationProcessor("org.projectlombok:lombok:1.18.26")
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

application {

}
distributions {
    main {
        contents {
            from("sql/hisMsg.db") {
                into("bin/sql")
            }
            from("src/main/resources") {
                into("bin/config")
                exclude("mapper")
            }
            from("front/dist") {
                into("bin/front/dist")
            }
        }
    }
}

springBoot {

}

tasks {
    val jvmVersion = "17"
    withType<JavaCompile> {
        options.apply {
            isVerbose = true
            encoding = "UTF-8"
        }
        sourceCompatibility = jvmVersion
        targetCompatibility = jvmVersion
    }
    withType<Test> {
        maxParallelForks = Runtime.getRuntime().availableProcessors()
        useJUnitPlatform()
    }
}
