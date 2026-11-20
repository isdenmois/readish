# AGENTS.md

Readish is an Android home-screen app for Onyx Boox e-readers. It shows reading progress from the stock reader (`com.neverland.alreaderprofs`) and accepts book uploads over Wi-Fi. Kotlin + Jetpack Compose + Hilt.

## Build & verify

- JDK 11 is required (Gradle 7.2 + AGP 7.1.3); `mise.toml` pins `java = "11"` and `task = "3"`.
- `task build` produces the signed release APK at `app/build/outputs/apk/release/app-release.apk` (compileSdk 31, minSdk 28) and prints its size.
- Debug builds are plain Gradle: `./gradlew :app:assembleDebug` → `app/build/outputs/apk/debug/app-debug.apk`.
- `Taskfile.yml` wraps the common jobs: `task build`, `task lint` (ktlint + Android lint), `task test`, `task version:show`, `task clean:apk`. Use the Gradle commands directly if Go Task isn't installed.
- There are no tests and no CI. Do not claim a test suite; verify with `assembleDebug`.
- ktlint runs on every subproject with `ignoreFailures = true`, so style never fails the build. Format with `./gradlew ktlintFormat`. Rules: 4-space indent, 120 columns (`.editorconfig`).
- ktlint's filter includes only `**/kotlin/**`, while all sources live under `src/main/java`; expect ktlint to skip most files.
- `ktlintCheck` and `lint` resolve extra artifacts on first use; TLS/resolution errors there are environmental, not code failures.

## Release & signing

- `task deploy` (`scripts/deploy.sh`) requires a clean tree, then bumps `versionCode` and `versionName` in `app/build.gradle`, builds the signed release APK, commits the bump ("Version Bump"), and uploads it to `SERVER_URL`. It creates a git commit and a network upload, so don't run it unless asked.
- Any failure after the bump restores the pre-deploy tree: a failed build checks out `app/build.gradle`, a failed upload runs `git reset --hard HEAD~1`. Re-running deploy therefore produces the same version.
- Copy `.env.defaults` to `.env`; Task loads it via `dotenv` (no manual export). Missing variables stop the task through `requires`.
- `.env` beats exported shell variables, so override a single value with Task CLI vars: `task build KEY_STORE_FILE=/path/to.jks`.
- `task build` needs `KEY_STORE_FILE`, `KEY_ALIAS`, `KEY_PASSWORD`; `task upload` needs `SERVER_URL` and uploads `app/build/outputs/apk/release/app-release.apk` unless `APK=path` is given.
- Signing is injected as `android.injected.signing.*` Gradle properties by `Taskfile.yml`; there is no `signingConfig` block.
- Partial tasks: `task build` (release build only), `task upload`, `task version:bump` (edit only, no commit), `task clean:apk` (deletes every APK in the repo).

## Dependencies

- Module files do not contain versions: refreshVersions 0.40.1 resolves `:_` placeholders and accessors (`AndroidX.compose.ui`, `Google.dagger.hilt.android`, ...) from `versions.properties`.
- Add new dependencies with `:_`, then run `./gradlew refreshVersions` to resolve/update them. Never hardcode versions or restructure the generated `####` headers in `versions.properties`.
- `compose_version` is derived via `versionFor(AndroidX.compose.ui)`; a Kotlin upgrade must be paired with a compatible Compose compiler.
- Deliberately old stack: Compose Material 1 (`androidx.compose.material.*`, not Material3), Kotlin 1.6.10, no version catalog, no Navigation library (separate Activities).

## Modules

- `app` — application id `com.isdenmois.readish`; `ReadishApplication` (`@HiltAndroidApp`), `MainModule` (provides SharedPreferences), `MainActivity`, and the merged manifest live here.
- `shared` — Compose theme/components and the `books_dir` resource; package `com.isdenmois.readish.ui`.
- `feature:home` — book list/repository and external reader integration; depends on `:shared` and `:feature:transfers`.
- `feature:transfers` — embedded NanoHTTPD upload server and QR screen; depends on `:shared`.
- Hilt is used throughout (`@HiltViewModel`, `@ApplicationContext` injection). Repositories are concrete classes with no interfaces or fakes.

## Domain & device quirks

- "My Books" comes from the stock reader's content provider (`content://com.neverland.alreaderprofs.BooksProvider/recent`, see `RecentTable`). There is no local book database, so a normal emulator shows an empty list.
- `books_dir` is variant-specific: debug `/storage/emulated/0/Download`, release `/storage/emulated/0/WifiTransfer`. Wi-Fi uploads and the latest-added list use this path.
- Upload server listens on port 8083 (multipart field `file`); it starts in `TransfersViewModel.init` and stops in `onCleared`.
- `EBookParser.cacheDirectory` must be set before parsing books (done in `HomeViewModel.init`).
- The app declares `CATEGORY_HOME`, so installing it may replace the device's home screen. It relies on `MANAGE_EXTERNAL_STORAGE` and `requestLegacyExternalStorage`.
- Opening a book/file is done with raw `file://` URIs after `ReadishApplication` disables `StrictMode` file-URI death.
