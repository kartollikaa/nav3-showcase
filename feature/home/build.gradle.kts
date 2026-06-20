plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.skydoves.navgraph)
}

android {
    namespace = "io.dodo.nav3.feature.home"
    compileSdk = 36
    defaultConfig { minSdk = 24 }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures { compose = true }
}

kotlin { jvmToolchain(17) }

dependencies {
    implementation(project(":core:designsystem"))

    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
    // navgraph's device-free renderer needs @Preview (ui-tooling-preview) AND the ComposeViewAdapter
    // from ui-tooling on its render classpath, so ui-tooling is a full implementation dep here.
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.ui.tooling)

    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)  // DialogSceneStrategy (the confirm dialog)
    implementation(libs.kotlinx.serialization.core)
}
