# Firebase Setup — Daily Zikr Tracker

## 1. Create Firebase project

Go to the Firebase Console and create a project.

## 2. Add Android app

Use this package name exactly:

`com.example.dailyzikrtracker`

Download `google-services.json` and copy it here:

`app/google-services.json`

## 3. Enable Google login

Firebase Console → Authentication → Sign-in method → Google → Enable.

Make sure the Google provider is enabled.

## 4. Add SHA-1/SHA-256

For a local debug build, Android Studio/Gradle can be used to obtain the debug SHA certificates.

Firebase Console → Project settings → Your Android app → add the SHA-1 and SHA-256 certificates.

For a release build, add the release keystore SHA certificates too.

## 5. Firestore

Create a Cloud Firestore database.

Each user will have one document:

`users/{GoogleUserUid}`

Example data:

```text
darood: 1500
kalma: 300
astaghfar: 1200
ikhlas: 50
fatiha: 80
```

No daily/monthly records are stored.

## 6. Security rules

Use:

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

This keeps one user's Zikr data separated from another user's data.
