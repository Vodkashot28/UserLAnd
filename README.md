<p align="center">
  <img src="fastlane/metadata/android/en-US/images/featureGraphic_next.png" alt="UserLAnd-Next Logo" width="600"/>
</p>

# UserLAnd-Next

![Build](https://github.com/Vodkashot28/UserLAnd/actions/workflows/build.yml/badge.svg)

A maintained fork of [UserLAnd](https://github.com/CypherpunkArmory/UserLAnd), modernized for Android 13/14.  
Run full Linux distributions or specific applications directly on Android — no root required.

---

## ✨ Features
- Run full Linux distros (Debian 12 Bookworm, Ubuntu, Arch, etc.) or individual apps.
- Install/uninstall like a regular Android app.
- No root required — sandboxed and secure.
- Updated for modern Android APIs (13/14).
- AI/ML-ready Debian 12 rootfs (Python, NumPy, SciPy, pandas).
- CI/CD powered builds with reproducible APKs.

---

## 📦 Installation
Download the latest APK from [Releases](https://github.com/Vodkashot28/UserLAnd/releases).  
Install it on your Android device and start provisioning your Linux environment.

---

## 🗂️ Asset Repositories
Rootfs tarballs and helper scripts are hosted in separate repos and fetched at runtime:

| Distro | Repo |
|--------|------|
| Debian 12 (Bookworm) | [UserLAnd-Next-Assets-debian12](https://github.com/Vodkashot28/UserLAnd-Next-Assets-debian12) |
| Apps catalog | [UserLAnd-next-Assets-Support](https://github.com/Vodkashot28/UserLAnd-next-Assets-Support) |

---

## 🛠️ Development
Clone the repo and build locally:

```bash
git clone https://github.com/Vodkashot28/UserLAnd.git
cd UserLAnd
./gradlew assembleDebug
```

### Requirements
- Android Studio Hedgehog or later
- JDK 17 (or JDK 21)
- Android SDK 34 (compileSdk), minSdk 23
- Gradle 8.7

### Tech Stack
- Kotlin 1.9.23 + Java 17 (`jvmTarget = "17"`)
- AndroidX Lifecycle 2.8.3 (ViewModel, LiveData)
- Room 2.6.1, OkHttp 4.12.0, Moshi 1.15.1
- Billing 7.1.1 (Google Play Billing)
- Sentry 7.14.0 for crash reporting
- Coroutines 1.8.1
- Navigation 2.7.7

---

## 🔑 Release Signing (CI/CD)
Release APKs are signed via GitHub Actions. The following secrets must be set in the repository:

| Secret | Description |
|---|---|
| `RELEASE_KEYSTORE` | Base64-encoded keystore: `base64 -w 0 userland_keystore.jks` |
| `ANDROID_KEYSTORE_PASSWORD` | Keystore password |
| `ANDROID_KEY_ALIAS` | Key alias (check with `keytool -list -keystore userland_keystore.jks`) |
| `ANDROID_KEY_PASSWORD` | Key password |
| `SENTRY_DSN` | Sentry DSN for crash reporting |

The keystore file (`userland_keystore.jks`) and any `.txt` credential files must **never** be committed — they are covered by `.gitignore`.

---

## 🔒 Permissions
On Android 13+, the app uses scoped storage — no `READ/WRITE_EXTERNAL_STORAGE` required.  
A `POST_NOTIFICATIONS` prompt is shown on first launch for foreground service notifications.

---

## 🤝 Contributing
See [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines.  
All contributions must follow the [Code of Conduct](CODE_OF_CONDUCT.md).
