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
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            // ensure applicationIdSuffix is not set if you want to use the same FlutterEngineCache
            // applicationIdSuffix = ".debug"
        }
        // Add profile build type as recommended by flutter build aar output
        create("profile") {
            initWith(getByName("debug"))
        }
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

    // Flutter module dependencies via AARs
    // These configurations directly reference the AARs built by `flutter build aar`
    // and expect their transitive dependencies to be resolved from the repositories
    // configured in settings.gradle.kts (which includes the local Flutter repo).
    debugImplementation("com.example.receive_images_flutter_demo:flutter_debug:1.0") {
        // If the AAR itself has issues with its published POM and transitive dependencies,
        // this might be needed, but typically isn't for Flutter AARs.
        // isTransitive = false 
    }
    releaseImplementation("com.example.receive_images_flutter_demo:flutter_release:1.0") {
        // isTransitive = false
    }
    // If using a profile build type that consumes a Flutter profile AAR:
    // add("profileImplementation", "com.example.receive_images_flutter_demo:flutter_profile:1.0")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}