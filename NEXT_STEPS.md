# Next steps for Sundar Patro

Work that follows the 1.1.0 submission on 2026-11-10. Completed launch work is in [PLAY_STORE_CHECKLIST.md](PLAY_STORE_CHECKLIST.md).

---

## 1. Wait for Google review

- [ ] Check the developer account email and **Play Console > Inbox** for approval or questions. Do not edit the store listing or App content while the app is in review.
- [ ] On approval, confirm the listing shows **Sundar Patro** rather than the temporary `com.sundarpatro.app (unreviewed)` label.
- [ ] If Google rejects the build, read the reason in Play Console, fix the issue, increment `versionCode`, rebuild, and resubmit.

## 2. After the app is live

- [ ] Install the app from the Play Store on a phone and verify:
  - [ ] Today, Calendar, Festivals, Holidays, and More all load offline in airplane mode.
  - [ ] **More > Holiday reminders** requests notification permission, and the switch stays on after you allow it.
  - [ ] A reminder arrives around 6 PM the evening before the next festival or holiday. The first one is Chhath on the evening of 14 November 2026.
- [ ] Add a "Get it on Google Play" link with the Play Store URL to `docs/index.html` and `docs/support/index.html`, then push to `main`.
- [ ] Optional: add a Nepali `ne-NP` store listing with translated short and full descriptions.
- [ ] Optional: check **Quality > Android vitals** for crashes or ANRs during the first week.

## 3. Next release: in-app ads with AdMob

On 2026-11-10 we decided to launch ad-free first, then add ads in version 2.0.

**Design**
- Use an AdMob adaptive banner only, with no interstitials or rewarded ads.
- Place the banner inside the scrollable page above the footer and away from the bottom tab bar to avoid accidental clicks.
- Develop with Google's test ad IDs, and use the production AdMob App ID and banner ad unit ID for the release build. These IDs are not secrets.

**App changes**
- [x] Add the Google Mobile Ads SDK and the `INTERNET` permission. The SDK adds the `AD_ID` permission.
- [x] Add Google's User Messaging Platform consent form for European Economic Area and United Kingdom users before ads load.
- [x] Hide the banner when offline so the calendar keeps working without a network connection.

**Claims to update in the same release**
- [x] Feature graphic: remove the "100% OFFLINE • ZERO TRACKING • NO ADS" pills.
- [ ] Store short and full descriptions: remove claims of "No ads", "no tracking", "100% offline", and "no internet permission".
- [x] In-app More footer and in-app privacy screen.
- [x] `docs/privacy/index.html`, `privacy-policy.html`, `docs/support/index.html`, and `Sundar Patro.html`. Run `git grep -il "no ads\|zero tracking\|100% offline"` to check for remaining occurrences.

**Play Console updates when submitting**
- [ ] In App content, set **Ads** to Yes and **Advertising ID** to Yes for advertising.
- [ ] In **Data safety**, declare that device identifiers, app interactions, and diagnostics are collected and shared with AdMob for advertising, and encrypted in transit.
- [ ] Confirm that the target audience stays 13+, because selecting ages under 13 triggers the Families ads policy.

## 4. Every future release

1. Increment `versionCode` by 1 and update `versionName` in `app/build.gradle.kts`. Google Play rejects a version code that was already uploaded, even to a testing track.
2. In Android Studio, select **Build > Generate Signed App Bundle**. The Run button builds only a debug APK.
3. Upload the new `app/release/app-release.aab`. To reuse an already-uploaded bundle on another track, select **Add from library** instead of Upload.
4. Push `main` before submitting if the privacy policy changed, because GitHub Pages serves it from `docs/`.

## 5. Known limitations to revisit

- The app schedules reminders for the next 90 days each time you open it. If you do not open the app for over 90 days, reminders stop until you open it again.
- Reminder notifications use the language that was active in the app when the reminders were scheduled.
- Android may deliver a reminder up to an hour after 6 PM because the app uses inexact alarms rather than the exact-alarm permission.
