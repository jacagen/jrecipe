plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader

    // Should I include all plugins here??

    alias(libs.plugins.kotlinJvm) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
}