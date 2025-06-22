plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.serialization)
}

kotlin {
    jvm()
    js(IR) {
        browser() // or nodejs() if needed
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.kotlinx.datetime)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jvmMain.dependencies {
            implementation(libs.clikt)

            implementation(libs.jackson.kotlin)
            implementation(libs.jackson.jsonSchema)

            implementation(libs.langchain4j)
            implementation(libs.langchain4j.openAi)
            implementation(libs.langchain4j.kotlin)

            implementation(libs.mongodb.kotlin)
        }

        val jsMain by getting
    }
}

