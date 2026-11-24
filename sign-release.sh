#!/bin/bash
set -e

cd /Users/bbhanda1/sundar-patro

JARSIGNER="/Users/bbhanda1/.gradle/jdks/eclipse_adoptium-17-aarch64-os_x.2/jdk-17.0.21+9/Contents/Home/bin/jarsigner"
AAB="app/build/outputs/bundle/release/app-release.aab"

echo "Signing $AAB with sundarpatro-release.jks..."
"$JARSIGNER" \
  -sigalg SHA256withRSA \
  -digestalg SHA-256 \
  -keystore sundarpatro-release.jks \
  "$AAB" \
  sundarpatro

cp "$AAB" release/app-release.aab
cp "$AAB" app/release/app-release.aab

echo ""
echo "✅ Signed bundle ready to upload at:"
echo "   /Users/bbhanda1/sundar-patro/release/app-release.aab"
echo "   /Users/bbhanda1/sundar-patro/app/release/app-release.aab"
