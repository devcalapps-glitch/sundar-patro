# Google Play Store Release Checklist — Sundar Patro

Use this checklist to track requirements for publishing **Sundar Patro** (`com.sundarpatro.app`) to the Google Play Store.

---

## 1. Release Binary & Code Readiness

- [x] **Target SDK 36 (Android 16):** Exceeds Google Play minimum target requirement (API 34+).
- [x] **Minimal Permissions:** Fully offline (no INTERNET permission). Only `POST_NOTIFICATIONS` (asked when the user turns on Holiday reminders) and `RECEIVE_BOOT_COMPLETED` (re-arms reminders after restart). Data safety: still no data collected or shared.
- [x] **Version Code & Name:** Set to `versionCode = 2`, `versionName = "1.1.0"` (1.1.0 adds holiday reminders) in `app/build.gradle.kts`.
- [x] **ProGuard / R8 Enabled:** Enabled in `release` build type (`isMinifyEnabled = true`, `isShrinkResources = true`).
- [x] **Release Keystore Generation:** Created `sundarpatro-release.jks` (RSA 2048-bit, alias: `sundarpatro`, protected in `.gitignore`).
- [x] **Signed App Bundle (`.aab`):** Generated at `app/release/app-release.aab` (~2.7 MB, signed and shrink-optimized).

---

## 2. Store Graphic Assets

- [x] **App Icon (512 × 512 px):** Present at `store-assets/play-icon-512.png` (32-bit PNG with alpha).
- [x] **Feature Graphic (1024 × 500 px):** Generated at `store-assets/feature-graphic-1024x500.png` (24-bit PNG, no alpha, meets Google Play specs).
- [x] **Phone Screenshots (4 Ready):** Sourced and verified in `store-assets/phone-screenshots/`:
  - [x] `01-today-panchang-light.jpg`: Today / Panchang & Event View (Light Mode, 1004 × 1741 px)
  - [x] `02-month-calendar-light.jpg`: Month Calendar Grid View (Light Mode, 1041 × 2048 px)
  - [x] `03-today-panchang-dark.jpg`: Today / Panchang & Event View (Dark Mode, 914 × 1514 px)
  - [x] `04-month-calendar-dark.jpg`: Month Calendar Grid View (Dark Mode, 960 × 1880 px — aspect ratio <= 2:1 compliant)

---

## 3. Privacy Policy & Hosting

- [x] **Privacy Policy Drafted:** Available locally in [`docs/privacy/index.html`](docs/privacy/index.html) and [`privacy-policy.html`](privacy-policy.html) (modeled after standard production privacy policy; zero data collection/retention, no analytics, no ads).
- [x] **Public HTTPS URL (GitHub Pages):** Live publicly accessible URLs for Google Play Console:
  - **Primary Privacy Policy URL:** `https://devcalapps-glitch.github.io/sundar-patro/privacy/`
  - **Direct Root URL:** `https://devcalapps-glitch.github.io/sundar-patro/privacy-policy.html`
  - **GitHub Source:** [GitHub Docs Privacy Source](https://github.com/devcalapps-glitch/sundar-patro/blob/main/docs/privacy/index.html)
  - **Support & FAQ URL:** `https://devcalapps-glitch.github.io/sundar-patro/support/`

---

## 4. Google Play Console Form Declarations

- [ ] **App Details:**
  - **App name:** `Sundar Patro` (or `Sundar Patro - Nepali Calendar`)
  - **Short description (≤ 80 chars):** `Fast, beautiful, offline Nepali Bikram Sambat calendar and panchang.`
  - **Full description (≤ 4000 chars):** Detailed description of features, offline reliability, and sacred geometry brand.
- [ ] **Category & Tags:**
  - Category: `Productivity` or `Tools`
  - Tags: `Calendar`, `Events`, `Productivity`
- [ ] **App Content & Policies:**
  - **Privacy Policy:** Enter `https://devcalapps-glitch.github.io/sundar-patro/privacy/` in the Play Console under **Policy > App content > Privacy policy**.
  - **Data Safety:** Select **No** to all questions (app does not collect, transmit, or share user data).
  - **Ads:** Select **No, my app does not contain ads**.
  - **App Access:** Select **All functionality is available without special access**.
  - **Financial Features:** Select **No**.
  - **Government Features:** Select **No**.
  - **Target Audience:** Select **13+** (or General Audience).
  - **Content Rating (IARC):** Complete questionnaire (utility/reference category — rated Everyone / PEGI 3).

---

## 5. Rollout & Testing Track

- [ ] **Testing Requirements:**
  - If using a **Personal Google Play Developer Account** created after Nov 13, 2023: Run a **14-day Closed Test with at least 20 opted-in testers** before applying for production access.
  - If using an **Organization Account**: Deploy directly to Internal/Closed testing or Production.
- [ ] **Upload Bundle to Release Track:** Upload signed `.aab` to Internal/Closed/Production track.
- [ ] **Submit for Review:** Submit app release for Google Play review.
