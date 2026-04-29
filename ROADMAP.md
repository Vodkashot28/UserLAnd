# Roadmap: UserLAnd-Next

This document outlines the modernization plan for **UserLAnd-Next**, a maintained fork of UserLAnd.  
Our goal is to bring Linux-on-Android into 2026 with stability, security, and community-driven development.

---

## 📍 Phase 1: Foundation & Audit ✅
- Fork and stabilize the codebase.
- Audit dependencies (Gradle 8.7, Kotlin 1.9.23, Android libraries).
- Reproduce and document crashes on Android 13/14.
- Establish CI/CD pipeline with GitHub Actions.
- Ensure license compliance (CC-BY-SA-4.0 attribution).

---

## ⚙️ Phase 2: Compatibility Fixes ✅
- Adapted file handling to Android 11+ scoped storage (no `READ/WRITE_EXTERNAL_STORAGE` on API 33+).
- Rewrote background services for Android 12+ restrictions (`STOP_FOREGROUND_REMOVE`, `foregroundServiceType=specialUse`).
- Updated runtime permission requests (`POST_NOTIFICATIONS` on API 33+, `FLAG_IMMUTABLE` on PendingIntents).
- Replaced legacy networking stack with OkHttp 4.12.0.
- Migrated all dependencies to current stable versions (Room 2.6.1, Lifecycle 2.8.3, Billing 7.1.1, Coroutines 1.8.1, Sentry 7.14.0).
- Upgraded to Java 17 / Kotlin 1.9.23 / JVM target 17.
- Fixed all Gradle 8.7 build script incompatibilities (`buildConfig`, `namespace`, deprecated APIs).
- Added `de.undercouch.download` plugin for asset fetching.

---

## 🚀 Phase 3: Modernization (In Progress)
- Migrate UI to Jetpack Compose.
- Refactor provisioning logic into modular, testable components.
- Move assets to GitHub Releases or CDN for reliability.
- Integrate reproducible builds with GitHub Actions.

---

## 🔒 Phase 4: Security & Stability
- Enable GitHub secret scanning and push protection.
- Harden session isolation and sandboxing.
- Add robust logging and crash reporting (Sentry 7.14.0 integrated).
- Expand unit and integration test coverage.

---

## 🌐 Phase 5: Extensions & Community
- Explore Termux backend integration for CLI stability.
- Refresh GUI layer (VNC → Wayland/X11 alternatives).
- Build contributor documentation (`CONTRIBUTING.md`, architecture overview).
- Rebrand and publish under UserLAnd-Next identity.
- Grow community via Issues, Discussions, and pull requests.

---

## 📌 Deliverables
- **Phase 1–2**: Crash-free build running on Android 13/14. ✅
- **Phase 3–4**: Modernized, secure, testable app with CI/CD.
- **Phase 5**: Community-driven fork with long-term sustainability.

---

## 🗓️ Timeline
- **Q2 2026**: Foundation & Compatibility Fixes. ✅
- **Q3 2026**: Modernization & Security.
- **Q4 2026**: Extensions, Community, and Release milestones.

---

## ✅ Vision
UserLAnd-Next will extend the original UserLAnd vision with:
- Modern Android support (API 23–34+).
- Reproducible builds and provenance.
- Active community contributions.
- A secure, sandboxed Linux-on-Android experience.
