val gsonVersion: String by project
val okhttp3Version: String by project
val loggingInterceptorVersion: String by project
val jakartaVersion: String by project
val gsonFireVersion: String by project
val jacksonDatabindVersion: String by project

plugins {
    id("java")
    id("org.openapi.generator") version "7.6.0"
}

version = "1.0.0"

dependencies {
    implementation("com.google.code.gson:gson:$gsonVersion")
    implementation("com.squareup.okhttp3:okhttp:$okhttp3Version")
    implementation("com.squareup.okhttp3:logging-interceptor:$loggingInterceptorVersion")
    implementation("jakarta.annotation:jakarta.annotation-api:$jakartaVersion")
    implementation("io.gsonfire:gson-fire:$gsonFireVersion")
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonDatabindVersion")
}

repositories {
    mavenCentral()
}

val generateUserServiceDtos by tasks.registering(org.openapitools.generator.gradle.plugin.tasks.GenerateTask::class) {
    generatorName.set("java")
    inputSpec.set("${rootDir}/user-service/user-dto/openapi/user-service.yaml")
    outputDir.set(project.layout.buildDirectory.dir("generated-sources/userservice").get().asFile.absolutePath)
    apiPackage.set("com.userservice.api")
    modelPackage.set("com.userservice.dto")
    additionalProperties.put("useJakartaEe", "true")
}

tasks.named("compileJava") {
    dependsOn(generateUserServiceDtos)
}

sourceSets {
    main {
        java {
            srcDir(project.layout.buildDirectory.dir("/generated-sources/userservice/src/main/java"))
        }
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}