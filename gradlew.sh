#!/usr/bin/env bash

set -e

gradlew_dir="$SRCROOT/gradlew"
if [[ "$OSTYPE" == "darwin"* ]]; then
  cp "$ANDROID_SDK_PATH/gradlew" "$SRCROOT/gradlew" 2>/dev/null || true
  chmod +x "$SRCROOT/gradlew"
fi

cd "$SRCROOT"
./gradlew clean assembleDebug
