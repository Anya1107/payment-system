val logbackEncoderVersion: String by project
val gsonVersion: String by project
val jakartaVersion: String by project
val okhttp3Version: String by project
val loggingInterceptorVersion: String by project
val gsonFireVersion: String by project
val logbackAppenderVersion: String by project
val junitBomVersion: String by project
val keycloakTestContainerVersion: String by project
val nexusUsername: String by project
val nexusPassword: String by project
val openFeignVersion: String by project

plugins {
    id("java")
    id("org.springframework.boot") version "3.5.0"
    id("io.spring.dependency-management") version "1.1.4"
    id("org.openapi.generator") version "7.6.0"
    application
}

group = "com.anyamurzenok.paymentsystem"
version = "1.0.0"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(24))
    }
}

application {
    mainClass.set("com.example.IndividualsApplication")
}

repositories {
    mavenCentral()
    maven {
        isAllowInsecureProtocol = true
        url = uri("http://localhost:8085/repository/maven-releases/")
        credentials {
            username = nexusUsername
            password = nexusPassword
        }
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-logging")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.security:spring-security-oauth2-client")
    implementation("org.springframework:spring-tx")
    implementation("io.micrometer:micrometer-registry-prometheus")
    implementation("net.logstash.logback:logstash-logback-encoder:$logbackEncoderVersion")
    implementation("com.google.code.gson:gson:$gsonVersion")
    implementation("jakarta.annotation:jakarta.annotation-api:$jakartaVersion")
    implementation("com.squareup.okhttp3:okhttp:$okhttp3Version")
    implementation("com.squareup.okhttp3:logging-interceptor:$loggingInterceptorVersion")
    implementation("io.gsonfire:gson-fire:1.8.5")
    implementation("com.github.loki4j:loki-logback-appender:$logbackAppenderVersion")
    implementation(project(":common-module"))
    implementation("com.feign.clients:user-api-client:1.0.3")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign:$openFeignVersion")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation(platform("org.junit:junit-bom:$junitBomVersion"))
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.mockito:mockito-core")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.springframework.boot:spring-boot-starter-webflux")
    testImplementation ("com.github.dasniko:testcontainers-keycloak:$keycloakTestContainerVersion")
}

tasks.test {
    useJUnitPlatform()
}