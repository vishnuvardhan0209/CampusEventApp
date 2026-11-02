# Campus Event Discovery & Smart Recommendation App

A starter Android Studio project built with Java and XML layouts.

## Requirements
- Android Studio
- JDK 17 (Android Studio's bundled JDK is recommended)
- Android SDK Platform 35
- Android SDK Build-Tools installed through SDK Manager

## Open and run
1. Extract `CampusEventApp.zip`.
2. In Android Studio, choose **File > Open**.
3. Select the extracted `CampusEventApp` folder (the folder containing `settings.gradle`).
4. Allow Gradle to sync and download the Android Gradle Plugin if prompted.
5. Create/start an Android emulator or connect an Android phone.
6. Press **Run**.

## Features in this starter
- Sample campus event list
- Search by event title, category, and venue
- Category filtering
- Event detail dialog
- Save/unsave events during the current app session
- Basic rule-based event recommendations

## Notes
- The event names, dates, venues, and popularity counts are demonstration data.
- Favorites are held in memory and reset when the app process is restarted.
- There is no Firebase/backend integration, login, registration, or push notification yet.
- The project uses Android Gradle Plugin 8.6.1, Gradle 8.7 compatibility, Java 17, compile SDK 35, and minimum SDK 24.
