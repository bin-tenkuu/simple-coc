import org.gradle.api.JavaVersion.VERSION_17
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    val kotlinVersion = "1.8.0"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion
    id("io.ktor.plugin") version "2.1.1"
    id("com.google.devtools.ksp") version "1.8.0-1.0.8"
}

group = "com.github.bin"
version = "1.0.0"

repositories {
    mavenLocal()
    maven("https://maven.aliyun.com/repository/public") // 阿里云国内代理仓库
    mavenCentral()
}

dependencies {
    // kotlin
    val kotlinVersion = "1.8.0"
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")
    compileOnly("org.jetbrains:annotations:24.0.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:1.5.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.6.4")
    // sqlite
    implementation("org.xerial:sqlite-jdbc:3.40.1.0")
    implementation("org.ktorm:ktorm-core:3.6.0")
    implementation("org.ktorm:ktorm-support-sqlite:3.6.0")
    // implementation("org.ktorm:ktorm-ksp-api:1.0.0-RC3")
    // ksp("org.ktorm:ktorm-ksp-compiler:1.0.0-RC3")
    // ktor-server
    val ktorVersion = "2.2.4"
    implementation("io.ktor:ktor-server-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-netty-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-resources:$ktorVersion")
    implementation("io.ktor:ktor-server-websockets-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-compression-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-data-conversion:$ktorVersion")
    implementation("io.ktor:ktor-server-host-common-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-call-logging-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-status-pages-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-double-receive-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-partial-content-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-default-headers-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-conditional-headers-jvm:$ktorVersion")
    testImplementation("io.ktor:ktor-server-tests-jvm:$ktorVersion")
    // logback
    implementation("org.slf4j:slf4j-api:2.0.5")
    implementation("ch.qos.logback:logback-core:1.4.6")
    implementation("ch.qos.logback:logback-classic:1.4.6")
}

application {
    mainClass.set("com.github.bin.Server")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
    sourceSets {
        main { kotlin.srcDir("build/generated/ksp/main/kotlin") }
        test { kotlin.srcDir("build/generated/ksp/test/kotlin") }
    }
}

tasks {
    withType<JavaCompile> {
        options.apply {
            isVerbose = true
            encoding = "UTF-8"
        }
        sourceCompatibility = VERSION_17.toString()
        targetCompatibility = VERSION_17.toString()
    }
    withType<KotlinCompile> {
        kotlinOptions {
            verbose = true
            jvmTarget = VERSION_17.toString()
            // allWarningsAsErrors = true
            freeCompilerArgs = freeCompilerArgs + mutableListOf(
                // "-Xexplicit-api=strict",
                "-Xjsr305=strict",
                "-opt-in=kotlin.RequiresOptIn",
                "-Xcontext-receivers",
                // "-Xuse-k2"
            )
        }
    }
    getByName<Test>("test") {
        maxParallelForks = Runtime.getRuntime().availableProcessors()
        useJUnitPlatform()
    }
}
