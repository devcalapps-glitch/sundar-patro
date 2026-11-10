# Sundar Patro (सुन्दर पात्रो) - Android App

A native Android application wrapping the **Sundar Patro** Bikram Sambat calendar using AndroidX `WebViewAssetLoader`, compliant with Google Play Store standards.

---

## Project Specifications

- **Application ID:** `com.sundarpatro.app`
- **Target SDK:** 36 (Android 16)
- **Minimum SDK:** 24 (Android 7.0 - Covers >96% of active devices)
- **Architecture:** Local-first offline `WebViewAssetLoader` (`https://appassets.androidplatform.net/assets/index.html`)
- **System Integrations:**
  - Modern Edge-to-Edge window insets
  - Splash Screen API (`androidx.core:core-splashscreen`)
  - Predictive Android Back button / Gesture handling
  - Automatic Light/Dark mode support matching system & web toggle
  - Touch optimizations (`-webkit-tap-highlight-color: transparent`, tap selection locks)

---

## 🚀 How to Run in Android Studio

1. Open **Android Studio** (`/Applications/Android Studio.app`).
2. Select **Open** and choose the project folder:
   ```
   /Users/bbhanda1/Desktop/sundar-patro
   ```
3. Allow Android Studio to complete Gradle sync.
4. Select a connected device or launch an Android Virtual Device (Emulator).
5. Click the green **Run ▶** button (or press `Shift + F10`).

---

## 📦 How to Build the `.aab` for Google Play Store

Google Play requires an **Android App Bundle (.aab)** signed with a release keystore.

> **Before every upload after the first:** in `app/build.gradle.kts`, raise `versionCode` by 1 (Play rejects a bundle whose code was already used) and update `versionName` (the version users see, e.g. `1.0.1`). The first upload uses `versionCode = 1`.

### Option A: Using Android Studio GUI (Easiest)

1. In Android Studio, go to the top menu: **Build > Generate Signed Bundle / APK...**
2. Choose **Android App Bundle** and click **Next**.
3. Under **Key store path**:
   - If you don't have one yet, click **Create new...**
   - Save it (e.g. `sundarpatro-release.jks`), set a strong password, and enter your key alias (e.g., `sundarpatro`).
4. Select build variant: **release**.
5. Check both **V1 (Jar Signature)** and **V2 (Full APK Signature)** if shown.
6. Click **Finish**. Android Studio will output your `.aab` file inside `app/release/`.

### Option B: Using the Included Keystore Script

Run the generator script in terminal:
```bash
cd /Users/bbhanda1/Desktop/sundar-patro
./generate-keystore.sh
```

---

## 📋 Google Play Console Submission Checklist

A detailed, trackable release checklist is available in [PLAY_STORE_CHECKLIST.md](PLAY_STORE_CHECKLIST.md).

When setting up your app in the [Google Play Console](https://play.google.com/console):

1. **App Details:**
   - App name: `Sundar Patro` (or `Sundar Patro - Nepali Calendar`)
   - Short description: Fast, elegant offline Nepali Bikram Sambat calendar and panchang.
   - Default language: English or Nepali.

2. **Store Assets Required:**
   - App Icon: 512 × 512 px (32-bit PNG)
   - Feature Graphic: 1024 × 500 px (JPG or 24-bit PNG)
   - Screenshots: At least 2 phone screenshots (e.g. Month view and Today view)

3. **App Content Declarations:**
   - **Target Audience:** Everyone (13+ or General Audience)
   - **Data Safety:** Sundar Patro collects **0 user data**. In the Data Safety form, select:
     - *Does your app collect or share any of the required user data types?* -> **No**.
   - **Ads:** Select *No, my app does not contain ads*.
   - **Financial Features / Government Features:** Select *No*.
   - **Privacy Policy:** Required by Google Play. You can host a simple one-page policy stating that Sundar Patro is an offline calendar application that does not collect, transmit, or share personal data.

