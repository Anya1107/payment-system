plugins {
    id("java")
    id("org.openapi.generator") version "7.6.0"
}

group = "com.anyamurzenok.paymentsystem"
version = "1.0.0"

val generateIndividualsApiDtos by tasks.registering(org.openapitools.generator.gradle.plugin.tasks.GenerateTask::class) {
    generatorName.set("java")
    inputSpec.set("${rootDir}/individuals-api/openapi/individuals-api.yaml")
    outputDir.set(project.layout.buildDirectory.dir("generated-sources/individuals").get().asFile.absolutePath)
    apiPackage.set("com.individuals.api")
    modelPackage.set("com.individuals.dto")
    additionalProperties.put("useJakartaEe", "true")
}

//val generateServiceBDtos by tasks.registering(org.openapitools.generator.gradle.plugin.tasks.GenerateTask::class) {
//    generatorName.set("java")
//    inputSpec.set("${rootDir}/service-b/openapi/service-b.yaml")
//    outputDir.set("$buildDir/generated-sources/service-b")
//    apiPackage.set("com.dto.serviceb.api")
//    modelPackage.set("com.dto.serviceb.model")
//    additionalProperties.put("useJakartaEe", "true")
//}

tasks.named("compileJava") {
    dependsOn(generateIndividualsApiDtos)
}

sourceSets {
    main {
        java {
            srcDir(project.layout.buildDirectory.dir("/generated-sources/individuals/src/main/java"))
//            srcDir("$buildDir/generated-sources/service-b/src/main/java")
        }
    }
}