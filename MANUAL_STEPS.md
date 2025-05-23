```markdown
# MANUAL_STEPS.md: Diagnosing Flutter AAR Build Issues

This document outlines manual steps to diagnose and potentially resolve issues with `flutter build aar` not producing AARs with correctly linked or published transitive dependencies, specifically the `flutter_embedding_debug` (and related) artifacts.

**Goal:** Ensure that `flutter build aar --debug` and `flutter build aar --release` in your `receive_images_flutter_demo` project generate AARs and a local Maven repository (`build/host/outputs/repo`) that can be successfully consumed by the Android host app (`ShareImageDriverAndroidOuterApp`).

--- 

## Step 1: Verify Flutter Environment & SDK Health

1.  **Open your terminal.**
2.  **Navigate to your Flutter module directory:**
    ```bash
    cd /Users/neil/AndroidStudioProjects/receive_images_flutter_demo
    ```
3.  **Run Flutter Doctor for a detailed report:**
    ```bash
    flutter doctor -v
    ```
    *   **Look for:**
        *   Any `[✗]` or `[!]` symbols indicating problems.
        *   Correct Flutter version and channel (e.g., Stable channel is usually recommended unless you have specific needs).
        *   Android toolchain status (Android SDK, Java version, Android Studio plugins).
        *   Connected devices (though not strictly necessary for `build aar`).
    *   **Action:** Address any reported issues by following the suggestions from `flutter doctor`.

4.  **Check for Overriding Flutter Environment Variables:**
    *   In your terminal, check if any of these are set (and to what values):
        ```bash
        echo $FLUTTER_ENGINE
        echo $FLUTTER_LOCAL_ENGINE
        echo $FLUTTER_GIT_URL
        echo $FLUTTER_SDK_ROOT # Should point to your Flutter SDK installation
        ```
    *   **Look for:** Unexpected paths or custom engine versions.
    *   **Action:** If any of these (especially `FLUTTER_ENGINE` or `FLUTTER_LOCAL_ENGINE`) are set and you don't intend to use a custom engine, unset them for your current session or remove them from your shell profile (`.zshrc`, `.bash_profile`, etc.) and start a new terminal session. Standard Flutter development usually doesn't require these to be set.

## Step 2: Perform a Deep Clean of the Flutter Module

1.  **Ensure you are in the Flutter module directory:**
    ```bash
    cd /Users/neil/AndroidStudioProjects/receive_images_flutter_demo
    ```
2.  **Remove all previous build artifacts and caches:**
    ```bash
    flutter clean
    rm -rf .android/build .android/.gradle
    rm -rf build 
    ```
    *(The `build` directory at the root of the Flutter module is where `build/host/outputs/repo` is created)*

3.  **Get Flutter dependencies again:**
    ```bash
    flutter pub get
    ```

## Step 3: Build Debug and Release AARs

1.  **Still in the Flutter module directory (`receive_images_flutter_demo`):**
2.  **Build the Debug AAR:**
    ```bash
    flutter build aar --debug
    ```
    *   **Look for:** Successful completion (`✓ Built build/host/outputs/repo`). Note any warnings or errors carefully.

3.  **Build the Release AAR:**
    ```bash
    flutter build aar --release
    ```
    *   **Look for:** Successful completion (`✓ Built build/host/outputs/repo`).

## Step 4: Inspect the Generated Local Maven Repository and POM Files

This is the most critical diagnostic step.

1.  **Navigate to the local Maven repository generated by Flutter:**
    ```bash
    cd /Users/neil/AndroidStudioProjects/receive_images_flutter_demo/build/host/outputs/repo
    ```

2.  **Inspect the Debug AAR's POM file:**
    *   **Path to POM:** `com/example/receive_images_flutter_demo/flutter_debug/1.0/flutter_debug-1.0.pom`
    *   **Open this `.pom` file with a text editor.**
    *   **Look for:** The `<dependencies>` section. Specifically, find the dependency entry for `io.flutter:flutter_embedding_debug`. It will look something like this:
        ```xml
        <dependency>
          <groupId>io.flutter</groupId>
          <artifactId>flutter_embedding_debug</artifactId>
          <version>1.0.0-SOME_HASH_HERE</version> <!-- Note this exact version string -->
          <scope>compile</scope>
        </dependency>
        ```
        Also note the versions for `armeabi_v7a_debug`, `arm64_v8a_debug`, `x86_64_debug` if listed separately or if `flutter_embedding_debug` is a parent POM that pulls them in.
    *   **Crucial Question:** What is the exact `<version>` declared for `flutter_embedding_debug` (and its architecture-specific variants like `armeabi_v7a_debug`, etc.)? Does it match the hash `cf56914b326edb0ccb123ffdc60f00060bd513fa` that the Android host build is failing to find?

3.  **Verify Artifact Presence in the Local Repo:**
    *   Based on the `<groupId>`, `<artifactId>`, and `<version>` you found in the POM file (e.g., `io.flutter`, `flutter_embedding_debug`, `1.0.0-SOME_HASH_HERE`), check if the actual artifact files exist in this local `repo`.
    *   **Example path to check (replace `1.0.0-SOME_HASH_HERE` with what you found in the POM):**
        ```bash
        ls -l io/flutter/flutter_embedding_debug/1.0.0-SOME_HASH_HERE/
        ```
        You should see files like `flutter_embedding_debug-1.0.0-SOME_HASH_HERE.jar`, `flutter_embedding_debug-1.0.0-SOME_HASH_HERE.pom`, etc.
    *   Do the same for `armeabi_v7a_debug`, `arm64_v8a_debug`, and `x86_64_debug` using their respective versions found in the main `flutter_debug-1.0.pom`.
    *   **Crucial Question:** Are the exact versions of these Flutter engine artifacts, as declared in `flutter_debug-1.0.pom`, actually present in these directories within your local `repo`?

## Step 5: Analyze Findings

*   **Scenario 1: POM lists version X, but version X is NOT in the `repo/io/flutter/...` directories.**
    *   This means `flutter build aar` created a POM file that declares dependencies it did not actually publish to the local repository. This is a Flutter toolchain bug or a serious local Flutter SDK corruption.
    *   **Possible Actions:**
        *   Consider a full reinstall of the Flutter SDK.
        *   Try a different Flutter channel (e.g., if you are on `beta`, try `stable`, or vice-versa: `flutter channel stable && flutter upgrade && flutter doctor -v`).
        *   Report this as a bug to the Flutter team with detailed reproduction steps. 

*   **Scenario 2: POM lists version X, and version X IS in the `repo/io/flutter/...` directories, BUT the Android host build STILL fails to find version Y (e.g., the `cf56914b...` hash).**
    *   This implies that what `flutter build aar` *produces* (version X) is different from what the Android host build *expects* (version Y, like `cf56914b...`).
    *   This could happen if the `include_flutter.groovy` script (used when `implementation project(':flutter')` is active) or some other part of the Android host's build process is somehow still trying to force resolution of an older/different Flutter engine version, overriding what the AAR specifies.
    *   **Action:** Double-check that your Android host project (`ShareImageDriverAndroidOuterApp`) is *not* using `implementation project(':flutter')` anymore and is solely relying on the direct AAR dependencies: `debugImplementation("com.example.receive_images_flutter_demo:flutter_debug:1.0")` etc., as per our last Gradle setup.

*   **Scenario 3: POM lists version X, version X IS in the repo, and X *is* the `cf56914b...` hash (or whatever the host is looking for).**
    *   If this is the case, and the host build still fails, it would be extremely strange. It would point to a very subtle Gradle caching issue or a problem with how the host app's `settings.gradle.kts` is declaring the `maven { url = uri("../receive_images_flutter_demo/build/host/outputs/repo") }` (e.g., path issues, incorrect declaration order though unlikely if `PREFER_SETTINGS` is on).
    *   **Action:** In the Android host project, run `./gradlew :app:dependencies --configuration debugRuntimeClasspath` to see exactly where Gradle is attempting to find `io.flutter:flutter_embedding_debug:1.0.0-cf56914b...` and why it might be failing despite it (hypothetically) being in the local repo.

--- 

## Step 6: When to Re-engage with the AI Assistant

Once you have gone through these steps, especially **Step 4 and 5**, and you have a clear understanding of:

1.  What version of `flutter_embedding_debug` (and related artifacts) your `flutter_debug-1.0.pom` declares.
2.  Whether those exact declared versions are physically present in your local `../receive_images_flutter_demo/build/host/outputs/repo`.
3.  And ideally, `flutter build aar --debug` and `flutter build aar --release` complete without error, and you believe the local repository is correctly populated.

Then, use the following prompt for me:

```

I have completed the manual diagnostic steps for the Flutter AAR build. Here's what I found:

1. **`flutter_debug-1.0.pom` declares `io.flutter:flutter_embedding_debug` version:
   ** [Specify the exact version string you found, e.g., 1.0.0-abcdef123456]
2. **This version [IS/IS NOT] present
   in `../receive_images_flutter_demo/build/host/outputs/repo/io/flutter/flutter_embedding_debug/` (
   and for related architecture-specific artifacts).**
3. **Other observations:
   ** [Mention any other relevant findings or fixes you applied, e.g., "flutter doctor showed no issues", "I had to unset FLUTTER_ENGINE", "The hash cf56914b... was indeed missing from the repo initially but after a full clean and rebuild it is now present / still missing."]

Based on this, please help me proceed with integrating the Flutter AARs into the
`ShareImageDriverAndroidOuterApp` and generating the README files.

```

This detailed information will give me a much better basis to help you with the next steps for the Android host app. Good luck!

```