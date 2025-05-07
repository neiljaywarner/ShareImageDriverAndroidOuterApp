pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        google()
        mavenCentral()
        // Add the local Flutter AAR repository
        maven {
            url = uri("../receive_images_flutter_demo/build/host/outputs/repo")
        }
    }
}

rootProject.name = "ShareImageDriverAndroidOuterApp"
include(":app")

// Include the Flutter module. The path should be relative to settings.gradle.kts
// The include_flutter.groovy script handles the setup of the :flutter project
val flutterProjectRoot = settingsDir.parentFile.resolve("receive_images_flutter_demo")
val flutterIncludeGroovy = flutterProjectRoot.resolve(".android/include_flutter.groovy")
if (flutterIncludeGroovy.exists()) {
    apply(from = flutterIncludeGroovy)
} else {
    // Fallback or error if script not found - though flutter build aar should ensure it's there for module type.
    // For now, we assume direct AAR consumption if script is missing and :flutter project isn't defined by it.
    // However, the AAR approach below in app/build.gradle.kts is more direct for pre-built AARs.
    println("Flutter include script not found at: ${flutterIncludeGroovy.absolutePath}. Ensure Flutter module is correctly structured or use direct AAR dependency.")
}