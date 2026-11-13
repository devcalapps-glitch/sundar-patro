# Next Steps — Sundar Patro

What comes after the 1.1.0 submission (2026-11-10). Completed launch work is in [PLAY_STORE_CHECKLIST.md](PLAY_STORE_CHECKLIST.md).

---

## 1. Now: wait for Google review

- [ ] Watch the developer account email and **Play Console → Inbox** for approval or questions. Don't edit the store listing or App content while in review.
- [ ] On approval, confirm the listing shows **Sundar Patro** (not the temporary `com.sundarpatro.app (unreviewed)` name).
- [ ] If rejected: read the reason in Play Console, fix, bump `versionCode`, rebuild, resubmit.

## 2. Right after the app is live

- [ ] Install from the Play Store on a real phone and check:
  - [ ] Today, Calendar, Festivals, Holidays, More all load offline (airplane mode).
  - [ ] **More → Holiday reminders** asks for notification permission; the switch stays on after allowing.
  - [ ] A reminder arrives around 6 PM the evening before the next festival or holiday (first one: Chhath, evening of 14 Nov 2026).
- [ ] Add a "Get it on Google Play" link (with the Play Store URL) to `docs/index.html` and `docs/support/index.html`, then push to `main`.
- [ ] Optional: add a Nepali (ne-NP) store listing (translated short and full descriptions).
- [ ] Optional: watch **Quality → Android vitals** for crashes or ANRs in the first week.

## 3. Next release: in-app ads (AdMob)

Decided 2026-11-10: launch ad-free first, then add ads in a later release (owner calls it 2.0).

**Design**
- AdMob **adaptive banner only** (no interstitials or rewarded ads).
- Placed at the **top, under the status bar**, as a native view above the WebView, away from the bottom tab bar (avoids AdMob accidental-click policy issues).
- Develop with Google's test ad IDs; swap in the real **AdMob App ID** and **banner ad unit ID** (owner has the AdMob account) for the release build. These IDs are not secrets.

**App changes**
- [x] Add the Google Mobile Ads SDK; add `INTERNET` permission (`AD_ID` comes from the SDK).
- [x] Add Google's UMP consent form (required for EEA / UK users) before ads load.
- [x] Hide the banner gracefully when offline; the calendar must keep working with no network.

**Claims to update in the same release** (they become false once ads ship)
- [x] Feature graphic: remove "100% OFFLINE • ZERO TRACKING • NO ADS" pills.
- [ ] Store short and full descriptions ("No ads", "no tracking", "100% offline", "no internet permission").
- [x] In-app More footer ("100% Offline • Zero Tracking") and in-app privacy screen.
- [x] `docs/privacy/index.html`, `privacy-policy.html`, `docs/support/index.html`, `Sundar Patro.html` (run `git grep -il "no ads\|zero tracking\|100% offline"` to catch any others).

**Play Console when submitting**
- [ ] App content → **Ads: Yes**; **Advertising ID: Yes** (purpose: advertising).
- [ ] **Data safety:** declare device identifiers and app interactions/diagnostics collected and shared with AdMob for advertising; data encrypted in transit.
- [ ] Re-check target audience stays 13+ (under-13 brings the Families ads policy).

## 4. Every future release

1. Bump `versionCode` by 1 (and `versionName`) in `app/build.gradle.kts`. Play rejects a code that was already uploaded, even to a testing track.
2. Android Studio → **Build → Generate Signed App Bundle** (the ▶ Run button only makes a debug APK).
3. Upload the new `app/release/app-release.aab`. To reuse an already-uploaded bundle on another track, use **Add from library**, not Upload.
4. Push `main` before submitting if the privacy policy changed (GitHub Pages serves it from `docs/`).

## 5. Known limitations to revisit

- Reminders are scheduled for the next 90 days each time the app is opened; if the app isn't opened for over 90 days, reminders stop until it is.
- Reminder text uses the app language at the time it was scheduled.
- Android may deliver a reminder up to about an hour after 6 PM (inexact alarms, no exact-alarm permission).
