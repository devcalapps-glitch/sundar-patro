# Google Play Store Release Checklist — Sundar Patro

Use this checklist to track requirements for publishing **Sundar Patro** (`com.sundarpatro.app`) to the Google Play Store.

---

## 1. Release Binary & Code Readiness

- [x] **Target SDK 36 (Android 16):** Exceeds Google Play minimum target requirement (API 34+).
- [x] **Zero Dangerous Permissions:** Fully offline architecture in `AndroidManifest.xml` with no background or sensitive permissions.
- [x] **Version Code & Name:** Set to `versionCode = 1`, `versionName = "1.0.0"` in `app/build.gradle.kts`.
- [x] **ProGuard / R8 Enabled:** Enabled in `release` build type (`isMinifyEnabled = true`, `isShrinkResources = true`).
- [ ] **Release Keystore Generation:** Create `sundarpatro-release.jks` using `./generate-keystore.sh` or Android Studio.
- [ ] **Signed App Bundle (`.aab`):** Generate signed `.aab` (unsigned bundle built at `app/build/outputs/bundle/release/app-release.aab`).

---

## 2. Store Graphic Assets

- [x] **App Icon (512 × 512 px):** Present at `store-assets/play-icon-512.png` (32-bit PNG with alpha).
- [ ] **Feature Graphic (1024 × 500 px):** JPG or 24-bit PNG (no transparency). Required by Google Play before store listing can be published.
- [ ] **Phone Screenshots:** At least 2 phone screenshots (up to 8, 16:9 or 9:16 aspect ratio, min 320px, max 3840px).
  - [ ] Screenshot 1: Month Calendar Grid View
  - [ ] Screenshot 2: Today / Panchang & Event View

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
