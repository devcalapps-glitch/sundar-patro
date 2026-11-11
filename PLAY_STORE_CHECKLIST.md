# Google Play Store Release Checklist — Sundar Patro

Record of the first Play Store submission of **Sundar Patro** (`com.sundarpatro.app`).
**Status (2026-11-10): version 1.1.0 (versionCode 2) submitted to Production, in Google review.**
Upcoming work lives in [NEXT_STEPS.md](NEXT_STEPS.md).

---

## 1. Release Binary & Code Readiness

- [x] **Target SDK 36 (Android 16)**, minSdk 24.
- [x] **Minimal Permissions:** no INTERNET permission. Only `POST_NOTIFICATIONS` (asked when the user turns on Holiday reminders) and `RECEIVE_BOOT_COMPLETED` (re-arms reminders after restart). No advertising ID.
- [x] **Version:** `versionCode = 2`, `versionName = "1.1.0"` in `app/build.gradle.kts`.
- [x] **R8 / resource shrinking** enabled for `release`.
- [x] **Release keystore:** `sundarpatro-release.jks` (alias `sundarpatro`, git-ignored; keep a backup off this machine).
- [x] **Signed App Bundle:** `app/release/app-release.aab` (1.1.0, built 2026-11-10 21:28).

## 2. Store Graphic Assets

- [x] **App icon 512 × 512:** `store-assets/play-icon-512.png`
- [x] **Feature graphic 1024 × 500:** `store-assets/feature-graphic-1024x500.png` (refreshed 2026-11-10)
- [x] **Phone screenshots (4):** `store-assets/phone-screenshots/` (Today and Month, light and dark)

## 3. Privacy Policy & Hosting

- [x] **Privacy policy:** `https://devcalapps-glitch.github.io/sundar-patro/privacy/` (covers the optional notification reminders)
- [x] **Support page:** `https://devcalapps-glitch.github.io/sundar-patro/support/`

## 4. Google Play Console Declarations

- [x] **App created:** name `Sundar Patro`, package `com.sundarpatro.app`, App, Free, default language en-US.
- [x] **Store listing:** short and full descriptions (states "No ads, no tracking, no data collected"), icon, feature graphic, screenshots.
- [x] **App content:** privacy policy URL, Ads = **No**, Advertising ID = **No**, App access = all features without sign-in, Data safety = no data collected or shared, content rating, target audience, government / financial features = No.

## 5. Release

- [x] **Account type:** organization account, so no closed-testing requirement.
- [x] **Internal testing:** release 2 (1.1.0) uploaded (no testers added; not needed).
- [x] **Production:** release 2 (1.1.0) added from library, full rollout, 178 countries / regions including Nepal plus rest of world.
- [x] **Submitted for review** 2026-11-10; quick checks passed. Managed publishing is off, so it publishes automatically on approval.
