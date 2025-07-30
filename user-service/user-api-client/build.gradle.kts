val openFeignVersion: String by project
val nexusUsername: String by project
val nexusPassword: String by project

plugins {
    id("java")
    id("maven-publish")
    id("io.spring.dependency-management") version "1.1.4"
}

group = "com.example.clients"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign:$openFeignVersion")
    implementation(project(":common-module"))
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            groupId = "com.feign.clients"
            artifactId = "user-api-client"
            version = "1.0.3"
        }
    }

    repositories {
        maven {
            isAllowInsecureProtocol = true
            name = "user-client-api-repo"
            url = uri("http://localhost:8085/repository/maven-releases/")
            credentials {
                username = nexusUsername
                password = nexusPassword
            }
        }
    }
}
