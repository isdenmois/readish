# Readish

Android home-screen app for Onyx Boox e-readers: shows the stock reader's recent books and accepts book uploads over Wi-Fi.

Kotlin · Jetpack Compose (Material 3) · Hilt · KSP · Ktor · Navigation Compose.

### Build

Requires JDK 17.

```bash
cp .env.defaults .env   # fill in SERVER_URL, KEY_STORE_FILE, KEY_ALIAS, KEY_PASSWORD
task build              # signed release APK -> app/build/outputs/apk/release/
task deploy             # bump version, build, commit "Version Bump", upload
```

`task --list` shows every task (lint, test, version:show, clean:apk, ...).

### Verify

```bash
./gradlew test :app:assembleDebug
task lint                        # Spotless + Android lint
./gradlew :app:verifyRoborazziDebug   # screenshot baselines
```

Screenshot baselines live in `app/src/test/screenshots`; `./gradlew :app:recordRoborazziDebug` updates them.
