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
    // NOTE: the below line resolved a build errorr
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "ShareImageDriverAndroidOuterApp"
include(":app")
val filePath = settingsDir.parentFile.toString() + "/receive_images_flutter_demo/.android/include_flutter.groovy"
apply(from = File(filePath))

 