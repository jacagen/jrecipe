plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.serialization)
    application
}

group = "com.jacagen.jrecipe"
version = "1.0.0"
application {
    mainClass.set("com.jacagen.jrecipe.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

dependencies {
    implementation(projects.shared)
    implementation(libs.apache.pdfbox)
    implementation(libs.logback)
    implementation(libs.ktor.cors)
    implementation(libs.ktor.serverContentNegotiation)
    implementation(libs.ktor.serverCore)
    implementation(libs.ktor.serverNetty)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.mongodb.kotlin)
    implementation(libs.jackson.kotlin)
    implementation(libs.jackson.jsonSchema)
    implementation(libs.langchain4j)
    implementation(libs.langchain4j.openAi)
    implementation(libs.langchain4j.kotlin)
    implementation(libs.clikt)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.kotlinx.datetime)

    testImplementation(libs.ktor.serverTestHost)
    testImplementation(libs.kotlin.testJunit)
}