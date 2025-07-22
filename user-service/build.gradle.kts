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

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}