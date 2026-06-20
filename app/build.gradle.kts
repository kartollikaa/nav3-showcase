plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "io.dodo.nav3"
    compileSdk = 36

    defaultConfig {
        applicationId = "io.dodo.nav3"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }
    buildTypes {
        release { isMinifyEnabled = false }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(project(":feature:home"))
    implementation(project(":feature:auth"))
    implementation(project(":feature:catalog"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.fragment.ktx)

    // Navigation 2 — the framework owns the back stack here.
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // Material Components: required so BottomSheetDialogFragment + the Material3 XML theme exist.
    implementation(libs.google.android.material)
}
