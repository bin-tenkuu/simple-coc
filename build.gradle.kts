//val kotlinVersion = "1.9.0"
plugins {
//    val kotlinVersion = "1.9.0"
    id("java")
//    kotlin("jvm") version kotlinVersion
//    kotlin("plugin.spring") version kotlinVersion
    id("org.springframework.boot") version "3.0.5" apply false
    id("io.spring.dependency-management") version "1.1.0" apply false
}

group = "com.github.bin"
version = "1.0.0"

allprojects {
    apply(plugin = "java")
    repositories {
        mavenLocal()
        maven("https://maven.aliyun.com/repository/public") // 阿里云国内代理仓库
    }

    tasks {
        java {
            sourceCompatibility = JavaVersion.VERSION_21
            targetCompatibility = JavaVersion.VERSION_21
        }
        val jvmVersion = "21"
        withType<JavaCompile> {
            options.apply {
//                isVerbose = true
                encoding = "UTF-8"
            }
            sourceCompatibility = jvmVersion
            targetCompatibility = jvmVersion
        }
        withType<Test> {
            maxParallelForks = Runtime.getRuntime().availableProcessors()
        }
    }

}
