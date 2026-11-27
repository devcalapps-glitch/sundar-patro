# Google Play Store release checklist for Sundar Patro

Record of the first Play Store submission of Sundar Patro, `com.sundarpatro.app`.
**Status on 2026-11-10.** Version 1.1.0, `versionCode 2`, is submitted to Production and in Google review.
Upcoming work is in [NEXT_STEPS.md](NEXT_STEPS.md).

---

## 1. Release binary and code readiness

- [x] **Target SDK 36 for Android 16**, `minSdk 24`.
- [x] **Minimal permissions.** No `INTERNET` permission in 1.1.0. Uses only `POST_NOTIFICATIONS`, requested when the user turns on Holiday reminders, and `RECEIVE_BOOT_COMPLETED`, which re-arms reminders after a restart. No advertising ID.
- [x] **Version.** `versionCode = 2` and `versionName = "1.1.0"` in `app/build.gradle.kts`.
- [x] **R8 and resource shrinking.** Enabled for the `release` build.
- [x] **Release keystore.** `sundarpatro-release.jks` with alias `sundarpatro`, ignored by git. Keep a backup off this machine.
- [x] **Signed App Bundle.** `app/release/app-release.aab`, version 1.1.0, built on 2026-11-10 at 21:28.

## 2. Store graphic assets

- [x] **App icon, 512 × 512.** `store-assets/play-icon-512.png`.
- [x] **Feature graphic, 1024 × 500.** `store-assets/feature-graphic-1024x500.png`, updated on 2026-11-10.
- [x] **Phone screenshots.** Four screenshots in `store-assets/phone-screenshots/` covering Today and Month views in light and dark themes.

## 3. Privacy policy and hosting

- [x] **Privacy policy.** `https://devcalapps-glitch.github.io/sundar-patro/privacy/`, which covers the optional notification reminders.
- [x] **Support page.** `https://devcalapps-glitch.github.io/sundar-patro/support/`.

## 4. Google Play Console declarations

- [x] **App record.** Name `Sundar Patro`, package `com.sundarpatro.app`, App, Free, default language `en-US`.
- [x] **Store listing.** Short and full descriptions, icon, feature graphic, and screenshots.
- [x] **App content.** Privacy policy URL, Ads set to No, Advertising ID set to No, App access set to all features without sign-in, Data safety set to no data collected or shared, content rating, target audience, and government and financial features set to No.

## 5. Release

- [x] **Account type.** Organization account, which has no closed-testing requirement.
- [x] **Internal testing.** Release 2, version 1.1.0, uploaded with no testers required.
- [x] **Production.** Release 2, version 1.1.0, added from library for a full rollout across 178 countries and regions including Nepal.
- [x] **Submitted for review.** Submitted on 2026-11-10 and passed initial checks. Managed publishing is off, so the app publishes automatically on approval.
