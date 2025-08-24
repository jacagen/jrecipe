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
    implementation(platform(libs.langgraph4j.bom))

    implementation(projects.shared)
    implementation(libs.apache.pdfbox)
    implementation(libs.clikt)
    implementation(libs.jackson.kotlin)
    implementation(libs.jackson.jsonSchema)
    implementation(libs.kotlinx.datetime)
    implementation(libs.ktor.cors)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.serverContentNegotiation)
    implementation(libs.ktor.serverCore)
    implementation(libs.ktor.serverNetty)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.langchain4j)
    implementation(libs.langchain4j.openAi)
    implementation(libs.langchain4j.kotlin)
    implementation(libs.langgraph4j.agent.executor)
    implementation(libs.langgraph4j.core)
    implementation(libs.logback)
    implementation(libs.mongodb.kotlin)
    implementation(libs.plantuml)

    testImplementation(libs.ktor.serverTestHost)
    testImplementation(libs.kotlin.testJunit)
}