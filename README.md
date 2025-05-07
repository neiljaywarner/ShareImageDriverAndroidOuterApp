# Share Image Driver Android App

This Android application demonstrates how to receive shared images from other apps (like Google
Photos) and pass them to a Flutter module.

## Project Overview

This project consists of two main parts:

1. An Android host app (this repository) that can receive shared images
2. A Flutter module that displays the received image paths

The Android app acts as a bridge to receive images from Android's share sheet and then pass them to
Flutter using the Add-to-App approach.

## Prerequisites

- [Android Studio](https://developer.android.com/studio) (latest version)
- [Flutter SDK](https://docs.flutter.dev/get-started/install) (version 3.7.2 or higher)
- [Git](https://git-scm.com/downloads)
- macOS, Windows, or Linux

## Step-by-Step Setup Guide

### 1. Clone the Repositories

First, create a directory for your projects and clone both repositories:

```bash
# Create a directory for both projects (if it doesn't exist)
mkdir -p ~/AndroidStudioProjects
cd ~/AndroidStudioProjects

# Clone the Android host app
git clone https://github.com/yourusername/ShareImageDriverAndroidOuterApp.git

# Clone the Flutter module
git clone https://github.com/yourusername/receive_images_flutter_demo.git
```

### 2. Set up the Flutter Module

```bash
# Navigate to the Flutter module
cd ~/AndroidStudioProjects/receive_images_flutter_demo

# Get Flutter dependencies
flutter pub get

# Build the Flutter module as an AAR
flutter build aar
```

This command will build the Flutter module as an Android Archive (AAR) that can be used by the
Android app.

### 3. Set up the Android Host App

```bash
# Navigate to the Android host app
cd ~/AndroidStudioProjects/ShareImageDriverAndroidOuterApp

# Open the project in Android Studio
open -a "Android Studio" .
```

The Android Studio project should automatically detect the Flutter module dependency based on the
configuration in `app/build.gradle.kts`.

### 4. Build and Run the Android App

1. In Android Studio, select your connected Android device or an emulator.
2. Click the "Run" button or press Shift+F10.

The app should build and launch on your device or emulator.

## Testing the App

### Sharing Images from Google Photos

1. Open Google Photos or any other image gallery app on your device.
2. Select an image (or multiple images).
3. Tap the "Share" button.
4. Select the "Share Image Driver" app from the share sheet.
5. The app will launch, showing the shared image(s).
6. Tap "Open Flutter App" to view the images in the Flutter module.

### Expected Behavior

1. When images are shared with the app, it will display the number of received images in the Android
   UI.
2. After tapping "Open Flutter App", the Flutter UI will load with:
    - A horizontal image preview list at the top
    - A list of image file paths below

## How It Works

1. The Android app registers intent filters for `ACTION_SEND` and `ACTION_SEND_MULTIPLE` with image
   MIME types.
2. When images are shared, the MainActivity receives the intents and extracts the image URIs.
3. When "Open Flutter App" is tapped, the app launches FlutterActivity and passes the image URIs as
   string extras.
4. The Flutter module receives these paths and displays them in a horizontal ListView.

## Project Structure

### Android App

- `MainActivity.kt`: Handles receiving shared images and launching the Flutter module
- `MyApplication.kt`: Sets up the Flutter engine cache

### Flutter Module

- `main.dart`: Contains the UI for displaying the shared image paths

## Troubleshooting

### Common Issues

1. **Flutter module not found**: Ensure the path to the Flutter module is correct in
   `app/build.gradle.kts`.
2. **AAR files not generated**: Make sure you've run `flutter build aar` in the Flutter module.
3. **Images not displaying**: Check if the correct permissions are granted for accessing the images.

### Solutions

- Run `flutter clean` in the Flutter module directory, then `flutter build aar` again.
- Restart Android Studio and clear the Gradle cache.
- Check the logcat output for any specific error messages.

## Advanced Customization

### Modifying the Flutter UI

To customize the Flutter UI for displaying images:

1. Edit `~/AndroidStudioProjects/receive_images_flutter_demo/lib/main.dart`.
2. Rebuild the AAR using `flutter build aar`.
3. Rebuild the Android app.

### Adding More Features

- Image editing capabilities
- Image upload to cloud services
- Social sharing options

## License

This project is licensed under the MIT License - see the LICENSE file for details.