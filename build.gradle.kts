import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val kotlinVersion = "1.8.20"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
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
    // implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.2")
    // annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    // lombok
//    compileOnly("org.projectlombok:lombok:1.18.26")
//    annotationProcessor("org.projectlombok:lombok:1.18.26")
    // kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.20")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.8.20")
    compileOnly("org.jetbrains:annotations:24.0.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:1.5.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.6.4")
    // sql
    implementation("org.postgresql:postgresql:42.5.4")
    implementation("org.xerial:sqlite-jdbc:3.42.0.0")
    implementation("com.baomidou:mybatis-plus-boot-starter:3.5.3.1")
    // swagger
    implementation("com.github.xiaoymin:knife4j-openapi3-jakarta-spring-boot-starter:4.1.0")
    // hutool全家桶: https://hutool.cn/docs/#/
    // implementation("cn.hutool:hutool-core:5.8.15")
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
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
    withType<KotlinCompile> {
        kotlinOptions {
            verbose = true
            jvmTarget = jvmVersion
            // allWarningsAsErrors = true
            freeCompilerArgs = freeCompilerArgs + mutableListOf(
                    // "-Xexplicit-api=strict",
                    "-Xjsr305=strict",
                    "-opt-in=kotlin.RequiresOptIn",
                    // "-Xcontext-receivers",
                    // "-Xuse-k2"
            )
        }
    }
    withType<Test> {
        maxParallelForks = Runtime.getRuntime().availableProcessors()
        useJUnitPlatform()
    }
}
