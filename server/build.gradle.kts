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
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    // lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok:1.18.36")
    compileOnly("org.jetbrains:annotations:24.0.1")
    // sql
    implementation("org.postgresql:postgresql:42.7.4")
    implementation("org.xerial:sqlite-jdbc:3.47.1.0")
    implementation("com.baomidou:mybatis-plus-spring-boot3-starter:3.5.9")
    // implementation("com.baomidou:mybatis-plus-jsqlparser:3.5.9")
    // swagger
    implementation("com.github.xiaoymin:knife4j-openapi3-jakarta-spring-boot-starter:4.3.0")
    // org.mapstruct
    // compileOnly("org.mapstruct:mapstruct")
    // annotationProcessor("org.mapstruct:mapstruct:1.5.5.Final")
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
        }
    }
}

