val openFeignVersion: String by project
val nexusUsername: String by project
val nexusPassword: String by project

plugins {
    id("java")
    id("maven-publish")
    id("org.openapi.generator") version "7.6.0"
    id("io.spring.dependency-management") version "1.1.4"
    id("pl.allegro.tech.build.axion-release") version "1.14.3"
}

group = "com.example.clients"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign:$openFeignVersion")
}

openApiGenerate {
    generatorName.set("java")
    inputSpec.set("$rootDir/user-service/user-api-client/openapi/user-api.yaml")
    outputDir.set(project.layout.buildDirectory.dir("generated-sources/userclient").get().asFile.absolutePath)
    apiPackage.set("com.client.api")
    modelPackage.set("com.client.model")
    invokerPackage.set("com.client.invoker")

    configOptions.set(
        mapOf(
            "library" to "feign",
            "springCloudFeignClient" to "true",
            "interfaceOnly" to "true",
            "dateLibrary" to "java8",
            "useSpringBoot3" to "true",
            "useBeanValidation" to "true",
            "serializationLibrary" to "jackson"
        )
    )
}

tasks.named("compileJava") {
    dependsOn("openApiGenerate")
}

scmVersion {
    tag {
        prefix.set("")
    }
    versionIncrementer("incrementPatch")
}

tasks.named("currentVersion") {
    doNotTrackState("Axion plugin accesses .git directory directly, which is unsupported for tracked state in Gradle 8+.")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            groupId = "com.feign.clients"
            artifactId = "user-api-client"
            version = scmVersion.version
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
