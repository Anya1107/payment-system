val openFeignVersion: String by project
val nexusUsername: String by project
val nexusPassword: String by project
val gsonVersion: String by project
val okhttp3Version: String by project
val loggingInterceptorVersion: String by project
val jakartaVersion: String by project
val gsonFireVersion: String by project
val jacksonDatabindVersion: String by project
val openFeignOkhttpVersion: String by project
val jacksonDatabindNullableVersion: String by project
val jacksonDatatypeJsrVersion: String by project
val openfeignJacksonVersion: String by project
val javaxAnnotationApiVersion: String by project
val jakartaAnnotationApiVersion: String by project
val jakartaValidationApi: String by project
val versionFile = file("version.txt")
val currentVersion = versionFile.readText().trim()

plugins {
    id("java")
    id("maven-publish")
    id("org.openapi.generator") version "7.6.0"
    id("io.spring.dependency-management") version "1.1.4"
}

group = "com.example.clients"
version = currentVersion

sourceSets {
    main {
        java {
            srcDir(project.layout.buildDirectory.dir("generated-sources/userclient/src/main/java"))
        }
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign:$openFeignVersion")
    implementation("io.github.openfeign:feign-okhttp:$openFeignOkhttpVersion")
    implementation("com.google.code.gson:gson:$gsonVersion")
    implementation("com.squareup.okhttp3:okhttp:$okhttp3Version")
    implementation("com.squareup.okhttp3:logging-interceptor:$loggingInterceptorVersion")
    implementation("jakarta.annotation:jakarta.annotation-api:$jakartaVersion")
    implementation("io.gsonfire:gson-fire:$gsonFireVersion")
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonDatabindVersion")
    implementation("org.openapitools:jackson-databind-nullable:$jacksonDatabindNullableVersion")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonDatatypeJsrVersion")
    implementation("io.github.openfeign:feign-jackson:$openfeignJacksonVersion")
    implementation("javax.annotation:javax.annotation-api:$javaxAnnotationApiVersion")
    implementation("jakarta.annotation:jakarta.annotation-api:$jakartaAnnotationApiVersion")
    implementation("jakarta.validation:jakarta.validation-api:$jakartaValidationApi")
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
            "useJakartaEe" to "true",
            "library" to "feign",
            "springCloudFeignClient" to "true",
            "interfaceOnly" to "true",
            "dateLibrary" to "java8",
            "useSpringBoot3" to "true",
            "useBeanValidation" to "true",
            "serializationLibrary" to "jackson",
            "feignClientName" to "user-api",
            "feignClientUrl" to "http://localhost:8082"
        )
    )
}

fun incrementPatch(version: String): String {
    val parts = version.split(".").map { it.toInt() }.toMutableList()
    if (parts.size != 3) throw IllegalArgumentException("Version must have format MAJOR.MINOR.PATCH")
    parts[2] += 1
    return parts.joinToString(".")
}

tasks.register("bumpVersion") {
    group = "versioning"
    description = "Increments the PATCH version in version.txt"

    doLast {
        val newVersion = incrementPatch(currentVersion)
        versionFile.writeText(newVersion)
    }
}

tasks.named("compileJava") {
    dependsOn("openApiGenerate")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            groupId = "com.feign.clients"
            artifactId = "user-api-client"
            version = project.version.toString()
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
