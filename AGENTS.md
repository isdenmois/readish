# AGENTS.md

Readish is an Android home-screen app for Onyx Boox e-readers. It shows reading progress from the stock reader (`com.neverland.alreaderprofs`) and accepts book uploads over Wi-Fi. Kotlin + Jetpack Compose + Hilt.

## Build & verify

- JDK 17 is required (AGP 9.5.1). `mise.toml` pins `java = "17"` and `task = "3"`.
- Toolchain: Gradle 9.9.2, AGP 9.5.1, built-in Kotlin 2.2.10, KSP 2.3.14, Compose BOM 2026.11.01, `compileSdk 37.1`, `targetSdk 36`, `minSdk 28`.
- `task build` produces the signed release APK at `app/build/outputs/apk/release/app-release.apk` and prints its size.
- Debug builds are plain Gradle: `./gradlew :app:assembleDebug` → `app/build/outputs/apk/debug/app-debug.apk`.
- `Taskfile.yml` wraps the common jobs: `task build`, `task lint` (Spotless + Android lint), `task test`, `task version:show`, `task clean:apk`. Use the Gradle commands directly if Go Task isn't installed.
- There is no CI. Verify changes with `./gradlew test :app:assembleDebug`; for UI changes also run `./gradlew :app:verifyRoborazziDebug`.
- `org.gradle.parallel=false` and the IPv4/TLS 1.2 flags are workarounds for this environment's proxy, which drops some concurrent TLS handshakes. Don't re-enable parallel here without checking dependency resolution.
- Unit tests live in `feature/home/src/test/java`, `feature/transfers/src/test/java` and `app/src/test/java` (JUnit 4 + Robolectric 4.18.1 + MockK + `kotlinx-coroutines-test`). They are JVM-only, no emulator needed.
- Logic tests cover mime mapping, `Book` progress math, cursor mapping, caches, the stock-reader repository, the upload server, the Wi-Fi IP formatter, and ViewModel state transitions.
- Screenshot tests render the stateless `HomeContent` / `TransfersContent` composables under Robolectric. Baselines live in `app/src/test/screenshots`. `./gradlew :app:recordRoborazziDebug` rewrites them; `:app:verifyRoborazziDebug` compares.
- Spotless enforces ktlint 1.8 on `.kt` and `.kts`; `./gradlew spotlessApply` formats, `spotlessCheck` fails the build on violations. Rules: 4-space indent, 120 columns (`.editorconfig`). `@Composable` names are exempt from function naming.
- Android lint is a separate task (`./gradlew lint`); it is not part of `test`.

## Release & signing

- `task deploy` (`scripts/deploy.sh`) requires a clean tree, then bumps `versionCode` and `versionName` in `app/build.gradle.kts`, builds the signed release APK, commits the bump ("Version Bump"), and uploads it to `SERVER_URL`. It creates a git commit and a network upload, so don't run it unless asked.
- Any failure after the bump restores the pre-deploy tree: a failed build checks out `app/build.gradle.kts`, a failed upload runs `git reset --hard HEAD~1`. Re-running deploy therefore produces the same version.
- Copy `.env.defaults` to `.env`; Task loads it via `dotenv` (no manual export). Missing variables stop the task through `requires`.
- `.env` beats exported shell variables, so override a single value with Task CLI vars: `task build KEY_STORE_FILE=/path/to.jks`.
- `task build` needs `KEY_STORE_FILE`, `KEY_ALIAS`, `KEY_PASSWORD`; `task upload` needs `SERVER_URL` and uploads `app/build/outputs/apk/release/app-release.apk` unless `APK=path` is given.
- Signing is injected as `android.injected.signing.*` Gradle properties by `Taskfile.yml`; AGP 9.5.1 still honors them, so there is no `signingConfig` block.
- Partial tasks: `task build` (release build only), `task upload`, `task version:bump` (edit only, no commit), `task clean:apk` (deletes every APK in the repo).

## Dependencies

- All versions live in `gradle/libs.versions.toml`. Module files use catalog accessors only; never hardcode a version there.
- refreshVersions and `versions.properties` are gone. Don't reintroduce them.
- AGP's built-in Kotlin compiles Kotlin 2.2.10. Don't add the `org.jetbrains.kotlin.android` plugin; use `kotlin { compilerOptions { ... } }` if compiler options are needed.
- Compose is configured by the `org.jetbrains.kotlin.plugin.compose` plugin; there is no `composeOptions` block.
- Hilt uses KSP (`com.google.devtools.ksp`), not kapt. Hilt 2.60.1 is the current line.
- Ktor 3.3.3 is the last release built against Kotlin 2.2.x. Bumping Ktor to 3.4+ requires moving the Kotlin version that AGP embeds first.
- `EBookParser` still comes from JitPack. Modernizing and republishing it is a separate follow-up, not part of this repo.
- Deliberately current-but-conservative: Material 3 only (Material 1 was removed), no version catalog plugins, no Compose Multiplatform.

## Modules

- `app` — application id `com.isdenmois.readish`; `ReadishApplication` (`@HiltAndroidApp`), `MainModule`, and the single `MainActivity` that owns the type-safe `NavHost` (`HomeRoute`, `TransfersRoute`). The merged manifest lives here.
- `shared` — Material 3 e-ink theme and shared composables; owns the `books_dir` resource.
- `feature:home` — book list/repository, stock-reader integration, `HomeViewModel` and stateless `HomeContent`; depends on `:shared` and `:feature:transfers`.
- `feature:transfers` — Ktor upload server, QR generation, `TransfersViewModel` and stateless `TransfersContent`; depends on `:shared`.
- Hilt is used throughout (`@HiltViewModel`, `@ApplicationContext` injection). Repositories are concrete classes with no interfaces or fakes; tests use MockK.

## Domain & device quirks

- "My Books" comes from the stock reader's content provider (`content://com.neverland.alreaderprofs.BooksProvider/recent`, see `RecentTable`). There is no local book database, so a normal emulator shows an empty list.
- `books_dir` is variant-specific: debug `/storage/emulated/0/Download`, release `/storage/emulated/0/WifiTransfer`. Wi-Fi uploads and the latest-added list use this path.
- The upload server listens on port 8083. It starts when the transfers destination opens and stops when that destination is popped; uploads do not survive leaving the screen.
- On opening transfers, the app attempts `setWifiEnabled(true)`. Android 10+ ignores that call unless the OEM allows it, so the screen also offers a button to the system Wi-Fi panel when no address appears.
- `EBookParser.cacheDirectory` must be set before parsing books (done in `HomeViewModel.init`).
- The app declares `CATEGORY_HOME`, so installing it may replace the device's home screen. It relies on `MANAGE_EXTERNAL_STORAGE`; `requestLegacyExternalStorage` was removed because targetSdk 30+ ignores it.
- Opening a book/file is done with raw `file://` URIs after `ReadishApplication` disables `StrictMode` file-URI death. The stock reader needs real paths, so don't migrate this to SAF without device testing.
- `MainActivity` is `singleTop`; a new `ACTION_MAIN` (pressing Home) pops the back stack to the book list.
