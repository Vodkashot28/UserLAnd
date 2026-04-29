<p align="center">
  <img src="fastlane/metadata/android/en-US/images/featureGraphic_next.png" alt="UserLAnd-Next Logo" width="600"/>
</p>

# UserLAnd-Next

![Build](https://github.com/Vodkashot28/UserLAnd/actions/workflows/build.yml/badge.svg)

A maintained fork of UserLAnd, modernized for Android 13/14.  
Run full Linux distributions or specific applications directly on Android — no root required.

---

## ✨ Features
- Run full Linux distros (Debian, Ubuntu, Arch, etc.) or individual apps.
- Install/uninstall like a regular Android app.
- No root required — sandboxed and secure.
- Updated for modern Android APIs (13/14).
- CI/CD powered builds with reproducible APKs.

---

## 📦 Installation
Download the latest APK from [Releases](https://github.com/Vodkashot28/UserLAnd/releases).  
Install it on your Android device and start provisioning your Linux environment.

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
- JDK 17
- Android SDK 34 (compileSdk), minSdk 23

### Tech Stack
- Kotlin + Java 17 (`jvmTarget = "17"`)
- AndroidX Lifecycle 2.8 (ViewModel, LiveData)
- Room 2.6, OkHttp 4.12, Moshi 1.15
- Billing 7.1 (Google Play Billing)
- Sentry 7.x for crash reporting
- Coroutines 1.8

---

## 🔒 Permissions
On Android 13+, the app uses scoped storage — no `READ/WRITE_EXTERNAL_STORAGE` required.  
A `POST_NOTIFICATIONS` prompt is shown on first launch for foreground service notifications.
