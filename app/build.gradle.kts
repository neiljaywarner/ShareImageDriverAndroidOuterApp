plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.shareimagedriverandroidouterapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.shareimagedriverandroidouterapp"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false // Keep false for easier debugging of release issues first
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            // applicationIdSuffix = ".debug" // Keep commented for consistent FlutterEngineCache
        }
        // The 'profile' build type is typically managed by the Flutter build process itself
        // when depending on project(":flutter"). No need to declare it here unless specifically required
        // for other native (non-Flutter) parts of the app and it aligns with Flutter's profile builds.
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    sourceSets {
        getByName("main") {
            // Ensure this path correctly points to where Pigeon generates Java files
            // within your Flutter module structure.
            // Standard path for a module is .android/app/src/main/java
            java.srcDirs(
                "src/main/java",
                "../receive_images_flutter_demo/.android/app/src/main/java"
            )
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // Flutter module dependency as a source project
    // This relies on settings.gradle.kts correctly including the Flutter module
    // via include_flutter.groovy, which defines the ":flutter" project.
    implementation(project(":flutter"))

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}