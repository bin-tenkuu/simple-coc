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
    val jvmVersion = "21"
    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(jvmVersion))
        }
    }
    tasks {
        withType<JavaCompile> {
            options.apply {
                encoding = "UTF-8"
            }
            sourceCompatibility = jvmVersion
            targetCompatibility = jvmVersion
        }
    }

}
