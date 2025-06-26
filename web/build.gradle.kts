plugins {
    kotlin("multiplatform")
}

kotlin {
    js(IR) {
        browser {
            commonWebpackConfig {
                outputFileName = "main.js"
            }
            testTask {
                useKarma {
                    useChromeHeadless()
                }
            }
        }
        binaries.executable()
    }

    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(projects.shared)

                implementation(libs.kotlin.emotion)
                implementation(libs.kotlin.mui)
                implementation(libs.kotlin.mui.icons.material)
                implementation(libs.kotlin.mui.system)
                implementation(libs.kotlin.react)
                implementation(libs.kotlin.react.dom)
                implementation(libs.kotlin.react.router.dom)

                implementation(libs.kotlinx.coroutines.core)
                //implementation(libs.kotlinx.coroutines.react)

                implementation(libs.ktor.client.js)
                implementation(libs.ktor.client.logging)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.serialization.kotlinx.json)

                implementation(npm("react-markdown", "8.0.6"))
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}

