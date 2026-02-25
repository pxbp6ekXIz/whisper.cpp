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

## Bundle a model inside the APK

Place one Whisper `.bin` model file in:

- `app/src/main/assets/models/`

At startup, the app loads the first `.bin` file found in that folder.

Recommended for mobile speed/size tradeoff:

- `ggml-tiny.en.bin` or `ggml-base.en.bin`

## Build a universal APK

## How to get these changes on your machine

If you create a PR from your fork, the code lives on the PR branch. To use those updates locally:

```bash
git clone <your-fork-url>
cd whisper.cpp
git fetch origin
git checkout <pr-branch-name>
```

If you already cloned the repo:

```bash
git fetch origin
git checkout <pr-branch-name>
git pull
```

You can also checkout the PR directly on GitHub Desktop or from the PR page.
