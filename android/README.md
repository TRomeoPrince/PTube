# PTube Android

Initial Android shell for PTube.

## What it does

- Opens the hosted PTube web interface in an Android WebView.
- Requests the PTube server URL on first launch.
- Requires HTTPS.
- Saves the selected server URL locally.
- Supports JavaScript, DOM storage, inline media playback, and Android back navigation.

## Build requirements

- Android Studio compatible with Android Gradle Plugin 9.4.x
- JDK 17
- Android SDK API 37

## Build

Open the `android` folder in Android Studio and run the `app` configuration.

To create a debug APK from the command line after installing the Gradle wrapper:

```bash
./gradlew assembleDebug
```

The initial APK is intentionally a thin client. Native playback, downloads, notifications, deep links, and account features can be added progressively.
