# UserLAnd-Next — AGENTS.md

Run full Linux distros on Android (no root). Maintained fork modernized for Android 13/14.

## Primary focus: Debian 12

- **Debian 12 (Bookworm)** is the primary distro. On first "one-click" app launch, defaults `user`/`userland` are auto-filled (see `AppsStartupFsm.kt:87`).
- Rootfs tarballs hosted externally at [`UserLAnd-Next-Assets-debian12`](https://github.com/Vodkashot28/UserLAnd-Next-Assets-debian12).
- App list is **hardcoded locally** in `GithubAppsFetcher.kt`:

  | App | Type | Interface | Filesystem |
  |-----|------|-----------|------------|
  | `debian12` | Distribution | CLI + GUI | debian12 |
  | `zencode-server` | Development | CLI | debian12 |
  | `zencode-dashboard` | Development | CLI | debian12 |
  | `vscode-server` | Development | GUI | debian12 |
  | `fs-backup` | Utility | CLI | debian12 |

- App descriptions and startup scripts are written locally by `GithubAppsFetcher` at first refresh, icons fall back to the default launcher icon.

- To add a new app, edit `GithubAppsFetcher.fetchAppsList()` and add the corresponding description/script in `fetchAppDescription()`/`fetchAppScript()`.

## Asset hooking — how external assets connect

- **Native libs** (`jniLibs/`) are downloaded during `preBuild` via the Gradle `downloadAssets`/`fetchAssets` tasks. Asset version `v1.2.7`. Source: `UserLAnd-next-Assets-Support` releases.
- Rootfs **tarballs** are fetched at **runtime** by `AssetRepository` (uses `GithubApiClient` + `HttpStream`), triggered through the FSM states in `SessionStartupFsm`.
- To add a new distro or support asset, work through `AssetRepository` / `GithubApiClient` and the FSM states in `SessionStartupFsm`.

## Build

| Command | Purpose |
|---|---|
| `./gradlew assembleDebug` | Debug APK (no signing) |
| `./gradlew assembleRelease` | Release APK (requires env signing vars) |
| `./gradlew test` | Unit tests only |
| `./gradlew connectedAndroidTest` | Instrumented tests (device/emulator) |
| `./gradlew testAll` | Unit + instrumented tests |
| `./gradlew ktlint` | Kotlin style check (ktlint 0.32.0) |
| `./gradlew jacocoCoverageReportForCi` | CI coverage (unit tests only) |

**Always run**: `./gradlew ktlint test` before pushing.

**Prerequisites**: JDK 17+, Android SDK 34, Gradle 8.7 (bundled wrapper).

## Architecture

- **Single Activity** (`MainActivity.kt`) + Jetpack Navigation (fragment nav graph)
- **MVVM** with ViewModels exposing `LiveData` — **no DI framework**, deps created manually by `lazy` delegates or factory classes
- **No custom `Application`** subclass
- **Two Finite State Machines** orchestrate all session/app startup flows:
  - `SessionStartupFsm` — distro selection → asset download → verify → extract → launch
  - `AppsStartupFsm` — one-click app setup → credential check → service type → script → launch
  - Both use sealed class `State`/`Event` hierarchies with validated transitions
- `MainActivityViewModel` merges both FSMs into one unified `LiveData<State>` via `MediatorLiveData`
- **Room DB v7** (`UlaDatabase`): 3 entities (`Session`, `Filesystem`, `App`), 6 migrations
- **Foreground service** (`ServerService`) runs proot'd Linux sessions, communicates via `LocalBroadcastManager`
- `buildConfigField` flags: `FORCE_PORTRAIT_GEOMETRY=true`, `MAX_DIMENSION=1280`, `MIN_DIMENSION=360`
- `minifyEnabled false` for all build types (debug/beta/release) — no ProGuard/R8

## Key packages

| Package | Contents |
|---|---|
| `tech.ula.model.entities` | Room entities + `ServiceType` sealed class |
| `tech.ula.model.daos` | Room DAOs |
| `tech.ula.model.repositories` | `UlaDatabase`, `AppsRepository`, `AssetRepository` |
| `tech.ula.model.state` | FSMs (`SessionStartupFsm`, `AppsStartupFsm`) |
| `tech.ula.model.remote` | `GithubApiClient`, `GithubAppsFetcher` |
| `tech.ula.viewmodel` | 7 ViewModels (all with `NewInstanceFactory`) |
| `tech.ula.ui` | 9 fragments + adapters |
| `tech.ula.utils` | `BusyboxExecutor`, `LocalServerManager`, `FilesystemManager`, `AssetDownloader`, `CredentialValidator`, etc. |

## Release signing

Signing is configured via **environment variables** (not gradle.properties):

```
SIGNING_STORE_FILE=<path-to-keystore>
SIGNING_STORE_PASSWORD=...
SIGNING_KEY_ALIAS=...
SIGNING_KEY_PASSWORD=...
```

Both `beta` and `release` signing configs read from these env vars. CI sets them from GitHub secrets (see `.github/workflows/build.yml`).

## Testing quirks

- **Unit tests**: `MockitoJUnitRunner`, `InstantTaskExecutorRule`, `runBlocking`, mockito-kotlin
- **Instrumented tests**: AndroidX Test Orchestrator, Room in-memory DB, Espresso + Barista
- `testInstrumentationRunnerArguments clearPackageData: 'true'`
- Room schema exports to `app/schemas/` (included in androidTest assets for migration tests)

## Misc

- Sentry DSN: empty by default, set via `sentryDsn` manifest placeholder or `SENTRY_DSN` env
- `scripts/replace_icons.sh` — batch-resize launcher icons (requires ImageMagick)
- `app/src/main/resources/lint.xml` — lint config
- `app/release/` and `app/src/main/jniLibs/` are gitignored (native libs downloaded at build time)
- ktlint version is pinned to **0.32.0** (quite old — some modern Kotlin syntax may trip it)
