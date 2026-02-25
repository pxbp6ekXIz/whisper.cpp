A minimal Android app using [whisper.cpp](https://github.com/ggerganov/whisper.cpp/) for fully offline speech-to-text.

## What this app does

- Single **Start/Stop recording** button.
- Transcribes recorded speech after you stop recording.
- Shows transcript in a read-only text box.
- **Copy text** button to copy the transcript.
- No in-app downloads required at runtime.

## Bundle a model inside the APK

Place one Whisper `.bin` model file in:

- `app/src/main/assets/models/`

At startup, the app loads the first `.bin` file found in that folder.

Recommended for mobile speed/size tradeoff:

- `ggml-tiny.en.bin` or `ggml-base.en.bin`

## Build a universal APK

This project already builds native libraries for:

- `arm64-v8a`
- `armeabi-v7a`
- `x86`
- `x86_64`

Build with Android Studio or:

```bash
./gradlew :app:assembleRelease
```

Then use the generated APK from `app/build/outputs/apk/release/`.

> Note: APK size depends heavily on the model file you embed.

(PS: Do not move this android project folder individually to other folders, because this android project folder depends on the files of the whole project.)
