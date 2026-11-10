#!/bin/bash
set -e

KEYSTORE_PATH="sundarpatro-release.jks"
ALIAS="sundarpatro"

if [ -f "$KEYSTORE_PATH" ]; then
    echo "Keystore $KEYSTORE_PATH already exists."
    exit 0
fi

echo "Generating release keystore for Google Play Store..."
keytool -genkeypair \
    -v \
    -keystore "$KEYSTORE_PATH" \
    -alias "$ALIAS" \
    -keyalg RSA \
    -keysize 2048 \
    -validity 10000 \
    -dname "CN=Sundar Patro, OU=Mobile, O=SundarPatro, L=Kathmandu, ST=Bagmati, C=NP"

echo "Release keystore generated successfully at $KEYSTORE_PATH!"
echo "Keep this keystore safe! Google Play requires this key for updates."

