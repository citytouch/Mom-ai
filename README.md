# Mom AI

A first-stage Marathi-first Android voice assistant designed for simple use.

## What this starter does

- Provides a large **बोला** microphone button.
- Requests Marathi (`mr-IN`) speech recognition through Android's available recognition service.
- Shows the recognised words on screen.
- Speaks a basic Marathi response when the device has Marathi text-to-speech data installed.
- Uses simple local phrase matching for a few sample responses.
- Includes a GitHub Actions workflow to build a debug APK in the cloud.

## What this starter does NOT do yet

- It does not place calls or control other apps.
- It does not have an always-on wake phrase.
- It does not identify or verify your mom's voice.
- Speech recognition availability and offline behavior depend on the phone's installed speech services and language packs.

## Build the APK on GitHub

1. Upload the contents of this folder to the root of your `Mom-ai` repository. Upload the files and folders, not the ZIP itself.
2. Open the repository's **Actions** tab.
3. Select **Build Mom AI APK** and press **Run workflow**, or make a commit to `main` to trigger it automatically.
4. When the run finishes successfully, open the run and download the artifact named `mom-ai-debug-apk`.
5. Extract the downloaded artifact ZIP to get `app-debug.apk`, then transfer it to the phone and install it. Android may ask you to allow installation from that source.

## Privacy and safety

- Do not commit voice recordings, voice profiles, API keys, passwords, or signing keys.
- Future voice enrollment should happen locally on the phone and include delete/re-enroll controls.
- Speaker verification is probabilistic and is not secure authentication; replay attacks and false matches are possible.
- A future calling feature must read back the contact name and ask for explicit confirmation before placing any call.
- Android restricts background microphone use. An always-listening wake phrase cannot be guaranteed in every device state.

## Technical details

- Native Android app written in Kotlin.
- Minimum Android version: Android 8.0 (API 26).
- Target/compile SDK: 35.
- Java 17, Android Gradle Plugin 8.7.3, Kotlin 2.0.21, Gradle 8.9.

This is a starter project, not a finished or production-hardened assistant.
