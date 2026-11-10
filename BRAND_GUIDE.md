# Sundar Patro (सुन्दर पात्रो) — Brand Identity Guide

> **Brand Direction:** The Minimal Focus Grid (बिन्दु र वृत्त)  
> **Brand Mission:** *"Reimagining Nepal's timeless calendar with modern elegance, quiet speed, and zero distractions."*

---

## 1. Brand Concept & Sacred Geometry

The brand mark for **Sundar Patro** synthesizes modern architectural minimalism (inspired by Cal.com and Swiss design principles) with traditional Nepali calendar concepts:

```
          [ ▀ ]       [ ▀ ]        <- Dual Binder Lugs
       ┌─────────────────────┐
       │   •       •       • │     <- 3×3 Architectural Grid of Days
       │   •      (◉)      • │     <- "Today" (आज): Crimson Focus Ring + Surya Gold Core
       │   •       •       • │
       └─────────────────────┘
```

### Key Symbolic Elements:
* **The 3×3 Grid of Nodes (बिन्दुहरू):** Represents the cyclic continuum of time, days of the week, and the structured precision of the Bikram Sambat calendar.
* **The Crimson Focus Ring (सिन्दूर वृत्त - `#E11D48`):** Encircles the center node to highlight **"Today" (*आज*)**, echoing the auspicious *Sindoor* and festive vitality of Nepali festivals.
* **The Surya Gold Core (सूर्य केन्द्र - `#F59E0B`):** Represents the Sun (*Surya*), the life-force behind the solar Bikram Sambat calendar.
* **Dual Binder Lugs:** Subtle architectural tabs at the top paying homage to traditional wall-hung Nepali patros.

---

## 2. Official Color Palette

| Token | Light Mode Hex | Dark Mode Hex | Meaning & Role |
| :--- | :--- | :--- | :--- |
| **`ink`** (Primary) | `#111111` | `#F9F9F7` | Deep contrast, typography, and primary iconography |
| **`canvas`** (Background) | `#FFFFFF` | `#121214` | Clean paper canvas; pure OLED in dark mode |
| **`brand_crimson`** | `#E11D48` | `#E11D48` | Focus ring, Saturdays (*शनिवार*), and national holidays |
| **`brand_gold`** | `#F59E0B` | `#F59E0B` | Core sun, auspicious *Sait / Subha Muhurat* |
| **`brand_grid_dot`** | `#CBD5E1` | `#52525B` | Muted background days and secondary time nodes |
| **`brand_frame_stroke`**| `#18181B` | `#3F3F46` | Calendar outline container |

---

## 3. Typography Standards

* **Devanagari (Primary):** **`Mukta`** (`wght@400;500;600;700`)
  * Clean, highly readable Devanagari script designed by Ek Type. Excellent clarity for *Tithi*, festival names, and Devanagari numerals.
* **Latin / Numerals (Secondary):** **`Inter`** or native system font (`-apple-system`, `Roboto`, `Segoe UI`)
  * Used for Gregorian AD dates, English headers, and technical settings. Tabular figures ensure aligned calendar columns.

---

## 4. Android App Assets Implemented

The following vector assets are now active in the Android application:

1. **Adaptive Launcher Foreground:**
   [`app/src/main/res/drawable/ic_launcher_foreground.xml`](app/src/main/res/drawable/ic_launcher_foreground.xml)
   * White wall-patro page with an ink header, two Surya-gold binder rings and a gold sun (the solar Bikram Sambat). Fits inside the 66dp safe circle of the 108×108dp viewport.
2. **Adaptive Launcher Background:**
   [`app/src/main/res/drawable/ic_launcher_background.xml`](app/src/main/res/drawable/ic_launcher_background.xml)
   * Sindoor crimson gradient, `#E11D48` to `#9F1239`.
   * PNG fallbacks for Android 7.x live in `mipmap-*dpi/` and are rendered from the same design.
3. **Android 13+ Dynamic Themed Icon (Monochrome):**
   [`app/src/main/res/drawable/ic_launcher_monochrome.xml`](app/src/main/res/drawable/ic_launcher_monochrome.xml)
   * Automatically adapts to Material You system theme and user wallpaper colors.
4. **Adaptive Splash Screen Icon:**
   [`app/src/main/res/drawable/splash_icon.xml`](app/src/main/res/drawable/splash_icon.xml)
   * The page on a crimson disc, so it reads on both the light and dark splash backgrounds.

---

## 5. Google Play Store Guidelines

* **App Title:** `Sundar Patro` (or `Sundar Patro — Nepali Calendar`)
* **Short Description:** *Fast, distraction-free Nepali Bikram Sambat calendar and panchang.*
* **App Icon (512×512 px):** `store-assets/play-icon-512.png` (full-bleed square; Play applies its own mask).
* **Feature Graphic (1024×500 px):**
  * Left side: Clean title *"Sundar Patro — सुन्दर, शान्त र भरपर्दो पात्रो"*.
  * Right side: High-contrast mockup of the minimalist calendar UI with edge-to-edge support.
