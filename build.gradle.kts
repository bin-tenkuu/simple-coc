plugins {
    id("java")

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
    java {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }
    tasks {
        withType<JavaCompile> {
            options.apply {
                encoding = "UTF-8"
            }
            val jvmVersion = "21"
            sourceCompatibility = jvmVersion
            targetCompatibility = jvmVersion
        }
    }

}
