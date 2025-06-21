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

                implementation(libs.kotlin.react)
                implementation(libs.kotlin.react.dom)
                implementation(libs.kotlin.react.router.dom)
                implementation(libs.kotlin.emotion)
                implementation(libs.kotlin.mui) // ⬅️ move this here
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}

