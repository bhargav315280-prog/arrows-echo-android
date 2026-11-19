# Arrows: Echo — 500-Level Android Puzzle Game

## Quick Setup Before Publishing
1. **Replace `applicationId`** in `app/build.gradle.kts` (`com.arrowsecho.game` -> your own unique package name).
2. **Add your AdMob IDs**:
   - `app/src/main/AndroidManifest.xml`: replace `APPLICATION_ID` with your AdMob App ID.
   - `app/src/main/java/com/arrowsecho/game/MainActivity.kt`: replace `INTERSTITIAL_AD_UNIT` and `REWARDED_AD_UNIT` with your real Ad Unit IDs.
3. **Push to GitHub**: GitHub Actions (`.github/workflows/android-build.yml`) will automatically build `app-debug.apk` and `app-release.aab` under the **Actions** tab.
4. **Sign your `.aab` for Google Play**: Generate a `.jks` keystore in Android Studio (or `keytool`) and sign the release `.aab` before uploading to Play Console.
