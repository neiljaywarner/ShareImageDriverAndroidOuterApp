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
        // Local Flutter repo - include_flutter.groovy might also add this or similar
        // It's generally safe to keep if Flutter's build process also uses it.
        maven {
            url = uri("../receive_images_flutter_demo/build/host/outputs/repo")
        }
    }
}

rootProject.name = "ShareImageDriverAndroidOuterApp"
include(":app")

// Apply the Flutter include script to define the :flutter project
val flutterProjectRoot =
    settingsDir.parentFile.resolve("receive_images_flutter_demo") // Assuming it's a sibling
val flutterModuleAndroidDir = flutterProjectRoot.resolve(".android") // Standard for Flutter modules
val flutterIncludeGroovy = flutterModuleAndroidDir.resolve("include_flutter.groovy")

if (flutterIncludeGroovy.exists()) {
    apply(from = flutterIncludeGroovy)
} else {
    throw GradleException("Flutter include script not found at: ${flutterIncludeGroovy.absolutePath}. This is required for the source code dependency approach. Ensure 'receive_images_flutter_demo' is a Flutter module and is a sibling to this project.")
}