```markdown
# NON_AAR_README.md: Android Host App (Source Code Flutter Integration)

This document describes how to set up, build, and run the `ShareImageDriverAndroidOuterApp` (the Android host application) which integrates the `receive_images_flutter_demo` Flutter module by building the Flutter module from its source code.

This approach uses Gradle to compile the Flutter module as part of the Android application build, relying on the Flutter SDK being present and correctly configured.

**Current Status Note:** This project has encountered persistent build issues related to resolving specific Flutter engine artifacts (e.g., `io.flutter:flutter_embedding_debug:1.0.0-cf56914b...`). The steps below assume these underlying Flutter SDK/environment issues have been resolved by following the diagnostic steps in `MANUAL_STEPS.md` or `POSSIBLE_NEXT_STEPS.md`.

## Prerequisites

-   **Android Studio:** Latest stable version recommended (e.g., Jellyfish or newer).
-   **Flutter SDK:** Latest stable channel version. Ensure it's correctly installed and configured in your PATH. (Refer to `POSSIBLE_NEXT_STEPS.md` if using a fresh SDK).
-   **Java Development Kit (JDK):** Version compatible with your Android Gradle Plugin (usually JDK 17 for recent AGP versions).
-   **Git:** For cloning the repositories.
-   **Connected Android Device or Emulator:** For running and testing the app.

## Setup Instructions

### 1. Clone Repositories

Both projects should be cloned as siblings in a common parent directory (e.g., `~/AndroidStudioProjects`).

```bash
# Navigate to your preferred projects directory
cd ~/AndroidStudioProjects # Or your chosen directory

# Clone the Android host app repository
# Replace YOUR_USERNAME/ShareImageDriverAndroidOuterApp.git with the actual URL
git clone https://github.com/YOUR_USERNAME/ShareImageDriverAndroidOuterApp.git

# Clone the Flutter module repository
# Replace YOUR_USERNAME/receive_images_flutter_demo.git with the actual URL
git clone https://github.com/YOUR_USERNAME/receive_images_flutter_demo.git
```

### 2. Configure the Flutter Module (`receive_images_flutter_demo`)

1. **Navigate to the Flutter module directory:**
   ```bash
   cd /Users/neil/AndroidStudioProjects/receive_images_flutter_demo 
   # Or: cd receive_images_flutter_demo (if already in ~/AndroidStudioProjects)
   ```

2. **Ensure Flutter SDK is healthy (especially if you switched SDKs):**
   ```bash
   flutter doctor -v
   ```
   Address any reported issues.

3. **Get Flutter dependencies:**
   ```bash
   flutter pub get
   ```

4. **Generate Pigeon communication files:**
   The Pigeon files define the interface between Android/iOS and Flutter for passing image data.
   ```bash
   flutter pub run pigeon --input pigeons/image_messages.dart --dart_out lib/pigeon.dart --java_out ./.android/app/src/main/java/com/example/receive_images_flutter_demo/Pigeon.java --java_package com.example.receive_images_flutter_demo --swift_out .ios/Runner/Pigeon.swift
   ```
   *(Ensure the paths and package name match your project setup if they differ from the command
   above)*

### 3. Open and Build the Android Host App (`ShareImageDriverAndroidOuterApp`)

1. **Open the Android host app project in Android Studio:**
    * File > Open...
    * Navigate to and select the `ShareImageDriverAndroidOuterApp` directory.

2. **Gradle Sync:** Android Studio should automatically sync the Gradle project. This will compile
   the Flutter module from source as part of the process due to the
   `implementation(project(":flutter"))` dependency and the `include_flutter.groovy` script
   configured in `settings.gradle.kts`.
    * **Troubleshooting:** If Gradle sync fails with errors similar to the persistent "Could not
      find io.flutter:flutter_embedding_debug..." errors, the underlying Flutter SDK/environment
      issue is not yet resolved. Refer back to `MANUAL_STEPS.md` or `POSSIBLE_NEXT_STEPS.md`.

3. **Build Debug APK via Gradle (Terminal):**
    * Navigate to the Android host app directory in your terminal:
      ```bash
      cd /Users/neil/AndroidStudioProjects/ShareImageDriverAndroidOuterApp
      ```
    * Run the assembleDebug task:
      ```bash
      ./gradlew :app:assembleDebug
      ```
    * A successful build will place the debug APK at: `app/build/outputs/apk/debug/app-debug.apk`

4. **Build Debug APK via Android Studio:**
    * Select the `app` configuration in the toolbar.
    * Select your target device/emulator.
    * Build > Build Bundle(s) / APK(s) > Build APK(s).

## Installing and Running the App (Debug Version)

### 1. Using ADB (Android Debug Bridge)

1. **Ensure your Android device is connected and USB debugging is enabled, or an emulator is
   running.**
2. **Verify device connection:**
   ```bash
   adb devices
   ```
   You should see your device listed.
3. **Navigate to the APK location (if not already there):**
   The debug APK is typically found at
   `ShareImageDriverAndroidOuterApp/app/build/outputs/apk/debug/`.
4. **Install the APK:**
   ```bash
   # From within ShareImageDriverAndroidOuterApp directory
   adb install app/build/outputs/apk/debug/app-debug.apk
   ```
    * Look for a "Success" message.
    * If an older version is installed, you might need `adb install -r ...` to reinstall.

### 2. Running via Android Studio

1. Select your target device/emulator in Android Studio.
2. Click the "Run 'app'" button (green play icon) or press `Shift+F10`.
   This will build, install, and launch the app.

## Testing Image Sharing

1. **Launch the `ShareImageDriverAndroidOuterApp` on your device/emulator.**
   You should see the initial Android UI (likely showing "No images shared yet...").

2. **Open an app that can share images (e.g., Google Photos, Gallery).**

3. **Select one or more images.**

4. **Tap the "Share" icon.**

5. **From the Android Share Sheet, find and select `ShareImageDriverAndroidOuterApp` (the name might
   vary slightly based on its manifest label).**

6. **The `ShareImageDriverAndroidOuterApp` should come to the foreground.**
    * The Android UI part should update to show the number of received images and their URIs.

7. **Tap the "Open Flutter App" button.**
    * This will launch the `FlutterActivity`.
    * The Flutter UI should initialize and then display the shared image(s):
        * A horizontal list of image previews (if `Image.file` can access them).
        * A list of the image URIs and their MIME types as received via Pigeon.

## How it Works (Source Code Integration)

- The `ShareImageDriverAndroidOuterApp`'s `settings.gradle.kts` uses `include_flutter.groovy` from
  the `receive_images_flutter_demo/.android` directory. This script configures
  `receive_images_flutter_demo` as a `:flutter` Gradle subproject.
- `ShareImageDriverAndroidOuterApp/app/build.gradle.kts` depends on
  `implementation(project(":flutter"))`.
- When the Android app is built, Gradle also compiles the Flutter module from its Dart source code,
  including the Flutter engine and any plugins.
- `MainActivity.kt` in the Android app implements the `Pigeon.ImageHostApi` to provide image URIs to
  Flutter.
- The Flutter app (`main.dart`) uses `ImageHostApi` (the Dart client generated by Pigeon) to request
  the image data from the Android host when the Flutter screen loads.
- `FlutterActivity` hosts the Flutter UI.

## Troubleshooting Common Issues (Post-Build-Fix)

- **`Pigeon.java` not found / Unresolved reference to `Pigeon` in `MainActivity.kt`:**
    * Ensure `flutter pub run pigeon ...` was executed successfully in the Flutter module.
    * Verify that the `sourceSets` in `ShareImageDriverAndroidOuterApp/app/build.gradle.kts`
      correctly points to the directory where `Pigeon.java` is generated within the Flutter module (
      e.g., `../receive_images_flutter_demo/.android/app/src/main/java`).
    * Perform a Gradle Sync in Android Studio, or `./gradlew clean :app:assembleDebug`.

- **Flutter screen is blank or crashes immediately:**
    * Check Android Studio's Logcat for errors from `FlutterActivity` or the Flutter engine.
    * Ensure `MyApplication.kt` correctly pre-warms and caches the Flutter engine.
    * Ensure the Pigeon setup in `MainActivity.onCreate` (`Pigeon.ImageHostApi.setUp(...)`) is
      called correctly and before Flutter tries to make calls.

- **Images not displaying in Flutter, but paths are shown:**
    * `Image.file(File(path))` in Flutter can only display images if the `path` is a direct file
      system path that the Flutter app has permission to read. Content URIs (`content://...`) often
      do not resolve directly to file paths that `dart:io File` can access without special
      handling (e.g., copying to a temporary cache file, or using a plugin that can resolve content
      URIs to displayable images).
    * The current implementation uses `Image.file` directly, which might fail for many content URIs.
      This part of the Flutter UI would need enhancement for robust image display from all URI
      types.

```