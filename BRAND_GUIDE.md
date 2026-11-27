# Sundar Patro, सुन्दर पात्रो, brand identity guide

> **Brand direction.** The Minimal Focus Grid, बिन्दु र वृत्त.
> **Brand mission.** Build a clear, fast Nepali Bikram Sambat calendar with zero clutter.

---

## 1. Brand concept and geometry

The brand mark for Sundar Patro combines a clean calendar grid with traditional Nepali calendar symbols:

```
          [ ▀ ]       [ ▀ ]        Top binder lugs
       ┌─────────────────────┐
       │   •       •       • │     3×3 grid of days
       │   •      (◉)      • │     "Today" (आज): crimson focus ring and gold core
       │   •       •       • │
       └─────────────────────┘
```

### Symbolic elements

* **The 3×3 grid of nodes, बिन्दुहरू.** Represents the days of the week and the structure of the Bikram Sambat calendar.
* **The crimson focus ring, सिन्दूर वृत्त `#E11D48`.** Circles the center node to mark "Today", आज, using the color of Sindoor.
* **The Surya gold core, सूर्य केन्द्र `#F59E0B`.** Represents the Sun, Surya, which governs the solar Bikram Sambat calendar.
* **Dual binder lugs.** Top tabs that reference wall-hung Nepali patros.

---

## 2. Official color palette

| Token | Light mode hex | Dark mode hex | Meaning and role |
| :--- | :--- | :--- | :--- |
| **`ink`** | `#111111` | `#F9F9F7` | Primary text and icons |
| **`canvas`** | `#FFFFFF` | `#121214` | Page background in light and dark mode |
| **`brand_crimson`** | `#E11D48` | `#E11D48` | Focus ring, Saturdays, शनिवार, and national holidays |
| **`brand_gold`** | `#F59E0B` | `#F59E0B` | Core sun and auspicious Sait |
| **`brand_grid_dot`** | `#CBD5E1` | `#52525B` | Background days and secondary time nodes |
| **`brand_frame_stroke`**| `#18181B` | `#3F3F46` | Calendar outline container |

---

## 3. Typography standards

* **Devanagari primary font.** `Mukta` in weights 400, 500, 600, and 700.
  * Devanagari script designed by Ek Type for Tithi, festival names, and Devanagari numerals.
* **Latin and numeral secondary font.** `Inter` or the native system font: `-apple-system`, `Roboto`, `Segoe UI`.
  * Used for Gregorian dates, English headers, and settings. Tabular figures align the calendar columns.

---

## 4. Android app assets

The Android application uses four vector assets:

1. **Adaptive launcher foreground.**
   [`app/src/main/res/drawable/ic_launcher_foreground.xml`](app/src/main/res/drawable/ic_launcher_foreground.xml)
   * White wall-patro page with an ink header, two Surya-gold binder rings, and a gold sun. Fits inside the 66dp safe circle of the 108×108dp viewport.
2. **Adaptive launcher background.**
   [`app/src/main/res/drawable/ic_launcher_background.xml`](app/src/main/res/drawable/ic_launcher_background.xml)
   * Sindoor crimson gradient from `#E11D48` to `#9F1239`.
   * PNG fallbacks for Android 7.x are in `mipmap-*dpi/` and use the same design.
3. **Android 13+ dynamic themed icon, monochrome.**
   [`app/src/main/res/drawable/ic_launcher_monochrome.xml`](app/src/main/res/drawable/ic_launcher_monochrome.xml)
   * Adapts to the Material You system theme and user wallpaper colors.
4. **Adaptive splash screen icon.**
   [`app/src/main/res/drawable/splash_icon.xml`](app/src/main/res/drawable/splash_icon.xml)
   * Places the calendar page on a crimson disc so it is legible on both light and dark splash backgrounds.

---

## 5. Google Play Store guidelines

* **App title.** `Sundar Patro` or `Sundar Patro, Nepali Calendar`.
* **Short description.** Offline Nepali Bikram Sambat calendar and panchang.
* **App icon, 512×512 px.** `store-assets/play-icon-512.png` is a full-bleed square; Google Play applies its own mask.
* **Feature graphic, 1024×500 px.**
  * Left side shows the title "Sundar Patro, सुन्दर, शान्त र भरपर्दो पात्रो".
  * Right side shows a high-contrast mockup of the calendar interface.
