# Sundar Patro, सुन्दर पात्रो, Android app

Sundar Patro is a native Android application that wraps the Bikram Sambat calendar using AndroidX `WebViewAssetLoader` for the Google Play Store.

---

## Project specifications

- **Application ID.** `com.sundarpatro.app`.
- **Target SDK.** 36, which is Android 16.
- **Minimum SDK.** 24, which is Android 7.0 and covers over 96% of active devices.
- **Architecture.** Local-first offline `WebViewAssetLoader` serving `https://appassets.androidplatform.net/assets/index.html`.
- **System integrations.**
  - Edge-to-edge window insets.
  - Splash Screen API using `androidx.core:core-splashscreen`.
  - Predictive Android back button and gesture handling.
  - Light and dark mode support matching the in-app toggle.
  - Touch optimizations including `-webkit-tap-highlight-color: transparent` and tap selection locks.

---

## How to run in Android Studio

1. Open Android Studio at `/Applications/Android Studio.app`.
2. Select **Open** and choose the project folder:
   ```
   /Users/bbhanda1/sundar-patro
   ```
3. Wait for Android Studio to finish the Gradle sync.
4. Select a connected device or launch an Android Virtual Device emulator.
5. Click the **Run** button or press `Shift + F10`.

---

## How to build the `.aab` for Google Play Store

Google Play requires an Android App Bundle `.aab` signed with a release keystore.

> **Before every upload after the first.** In `app/build.gradle.kts`, raise `versionCode` by 1 because Google Play rejects a bundle whose code was already used, and update `versionName` to the version users see, such as `1.0.1`. The first upload uses `versionCode = 1`.

### Option A: Use the Android Studio interface

1. In Android Studio, open the top menu and select **Build > Generate Signed Bundle / APK...**
2. Choose **Android App Bundle** and click **Next**.
3. Under **Key store path**:
   - If you do not have a keystore yet, click **Create new...**
   - Save it as `sundarpatro-release.jks`, set a password, and enter your key alias `sundarpatro`.
4. Select the **release** build variant.
5. Check both V1 Jar Signature and V2 Full APK Signature if shown.
6. Click **Finish**. Android Studio writes the `.aab` file to `app/release/`.

### Option B: Use the keystore script

Run the generator script in the terminal:
```bash
cd /Users/bbhanda1/sundar-patro
./generate-keystore.sh
```

---

## Google Play Console submission checklist

The release checklist is in [PLAY_STORE_CHECKLIST.md](PLAY_STORE_CHECKLIST.md). Upcoming work, including post-launch checks, the ads release, and the per-release routine, is in [NEXT_STEPS.md](NEXT_STEPS.md).

When you set up the app in the [Google Play Console](https://play.google.com/console):

1. **App details.**
   - App name: `Sundar Patro` or `Sundar Patro, Nepali Calendar`.
   - Short description: Offline Nepali Bikram Sambat calendar and panchang.
   - Default language: English or Nepali.

2. **Store assets required.**
   - App icon: 512 × 512 px, 32-bit PNG.
   - Feature graphic: 1024 × 500 px, JPG or 24-bit PNG.
   - Screenshots: At least 2 phone screenshots, such as the Month view and Today view.

3. **App content declarations.**
   - **Target audience.** Everyone, 13+ or General Audience.
   - **Data safety.** Declare device identifiers and app interactions collected by Google AdMob for advertising, or select **No** for the initial ad-free release.
   - **Financial features and government features.** Select **No**.
   - **Privacy policy.** Hosted on GitHub Pages:
     - Live policy URL: [https://devcalapps-glitch.github.io/sundar-patro/privacy/](https://devcalapps-glitch.github.io/sundar-patro/privacy/)
     - Live support URL: [https://devcalapps-glitch.github.io/sundar-patro/support/](https://devcalapps-glitch.github.io/sundar-patro/support/)
     - Policy source: [`docs/privacy/index.html`](docs/privacy/index.html), also mirrored at [`privacy-policy.html`](privacy-policy.html).
