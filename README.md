# Android Assignment — OTP Logger (Kotlin + Jetpack Compose)

This app implements a passwordless authentication flow: Email → OTP → Session timer → Logout. No backend is required; all logic runs locally.

## App Flow

1. LoginScreen: enter email → Send OTP
2. OtpScreen: enter 6-digit OTP → verify / resend
3. SessionScreen: show start time + live duration (mm:ss) → logout

## 1) OTP logic and expiry handling

- OTP is a random 6-digit code.
- OTP TTL is 60 seconds (time is based on `System.currentTimeMillis()`).
- Each email gets a maximum of 3 attempts. Attempts decrement on each wrong OTP.
- On success, OTP data is cleared. On expiry, OTP data is also cleared.
- Resend OTP:
  - invalidates the previous OTP
  - resets attempts
  - resets expiry
- Bonus: resend cooldown is enforced (you need to wait before resending).

Implementation: `android/app/src/main/java/com/androidassignment/data/OtpManager.kt`

## 2) Data structures used (and why)

OTP is stored per email using:

- `Map<String, OtpData>` where key = normalized email (`trim().lowercase()`)
- Why:
  - clean per-email separation
  - O(1) lookups for generate/validate/snapshot
  - scales naturally for multiple emails

Implementation: `android/app/src/main/java/com/androidassignment/data/OtpManager.kt`

## 3) Which external SDK was chosen (and why)

I chose **Timber** because:

- lightweight and easy to set up
- great for local debugging/logging
- no dashboard/configuration overhead

Events logged:
- OTP generated
- OTP validation success
- OTP validation failure
- Logout

Implementation:
- Logger: `android/app/src/main/java/com/androidassignment/analytics/AnalyticsLogger.kt`
- Initialization: `android/app/src/main/java/com/androidassignment/App.kt`

## 4) What I used GPT for vs what I implemented and understood

Used GPT for:
- planning/checklist and rough structure guidance (screens, ViewModel, data layer, edge cases)
- quick reference for Compose patterns (state hoisting, LaunchedEffect usage)

Implemented and understood myself:
- full OTP rules (expiry/attempts/resend/cooldown) + unit tests
- ViewModel + sealed UI state flow (one-way data flow)
- Compose UI (keyboard handling, countdown UI, session timer)
- Timber integration + event logging
- session persistence (app restart keeps you logged-in) using DataStore

## Session persistence (app close/reopen safe)

After OTP verification, the session (email + startTime) is saved to DataStore. If the app is killed and reopened, the session is restored and the timer continues from the original start time.

Implementation: `android/app/src/main/java/com/androidassignment/data/SessionStore.kt`

## Run

Open `AndroidAssignment/android` in Android Studio, or run:

```sh
cd android
./gradlew assembleDebug
```

Install (physical device connected):

```sh
cd android
./gradlew installDebug
```

## Tests

```sh
cd android
./gradlew testDebugUnitTest
```
