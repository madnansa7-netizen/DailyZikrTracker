# Daily Zikr Tracker

An Android app for keeping an online lifetime total of daily Zikr.

## Included Zikr

- Darood Shareef
- Pehla Kalma
- Astaghfar
- Surah Ikhlas
- Surah Fatiha

## How it works

If the current Darood Shareef total is `500` and the user enters `1000`, the app saves:

`500 + 1000 = 1500`

The app intentionally does **not** keep date-wise or month-wise history. It stores only the running total for each Zikr.

## Technology

- Kotlin
- Jetpack Compose
- Firebase Authentication
- Google Sign-In
- Cloud Firestore

## Firebase setup

1. Create a Firebase project.
2. Add an Android app with package name:
   `com.example.dailyzikrtracker`
3. Download `google-services.json`.
4. Put `google-services.json` inside the `app/` folder.
5. In Firebase Authentication, enable Google as a sign-in provider.
6. Create/enable Cloud Firestore.
7. Add a Firestore rule so each signed-in user can read/write only their own document.

Recommended Firestore rule:

```text
rules_version = '2';

service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{userId} {
      allow read, write: if request.auth != null
                         && request.auth.uid == userId;
    }
  }
}
```

## Android Studio

Open the project root in Android Studio, let Gradle sync, then run it on an Android device/emulator.

## Important

`google-services.json` is intentionally not included in this project because it belongs to the Firebase project that will own the app's data.
