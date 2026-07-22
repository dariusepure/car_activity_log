# Implementation Plan - Protect Gemini API with Firebase App Check

Protect the Gemini AI integration by migrating to **Vertex AI for Firebase** and enabling **Firebase App Check**. This ensures that only your authentic, unmodified app can access the AI services, and it removes the need to store an API key in your code.

## User Review Required

> [!IMPORTANT]
> This migration requires manual configuration in the Firebase Console to work. If you don't do these steps, the AI features will stop working after the update.

### Required Manual Steps in Firebase Console:
1.  **Enable Vertex AI**:
    *   Go to the [Firebase Console](https://console.firebase.google.com/).
    *   Select your project.
    *   Go to **Build > Vertex AI**.
    *   Click **Get Started** and follow the instructions to enable the API and upgrade to the Blaze plan (if not already).
2.  **Configure App Check**:
    *   Go to **Build > App Check**.
    *   Click **Get Started**.
    *   Go to the **Apps** tab and register your Android app with **Play Integrity**.
    *   You will need to provide the **SHA-256** fingerprint of your signing certificate (both for Debug and Release).
3.  **Enforce Protection**:
    *   In the App Check **APIs** tab, find **Vertex AI** and click **Enforce**.

## Proposed Changes

### Dependency Management

#### [MODIFY] [libs.versions.toml](file:///D:/Car%20Activity%20Log/gradle/libs.versions.toml)
Ensure `firebase-vertexai` and `firebase-appcheck-playintegrity` are correctly referenced.

#### [MODIFY] [build.gradle.kts](file:///D:/Car%20Activity%20Log/app/build.gradle.kts)
- Add Vertex AI for Firebase and App Check dependencies.
- Remove the legacy `generativeai` dependency.

### Application Initialization

#### [MODIFY] [CarActivityLogApp.kt](file:///D:/Car%20Activity%20Log/app/src/main/java/com/dariusepure/caractivitylog/CarActivityLogApp.kt)
Initialize Firebase App Check with the `PlayIntegrityAppCheckProviderFactory`.

### AI Repository Migration

#### [MODIFY] [GeminiRepository.kt](file:///D:/Car%20Activity%20Log/app/src/main/java/com/dariusepure/caractivitylog/data/ai/GeminiRepository.kt)
Migrate from `com.google.ai.client.generativeai` to `com.google.firebase.vertexai`.
- Remove `apiKey` requirement (it will use the project's internal security).
- Use `Firebase.vertexAI.generativeModel(...)`.

## Verification Plan

### Automated Tests
- Build the project to ensure all dependencies are correctly resolved.
- Verify that the new `FirebaseVertexAI` usage compiles.

### Manual Verification
- **Debug Mode**: I will include a "Debug Provider" for App Check so it works on your emulator/test device without Play Integrity enforcement during development.
- **Verification**: Once you enable App Check in the console, try to use the "Scan Document" or "AI Diagnosis" features. If App Check is working, the requests will pass. If the app is modified or unauthorized, the requests will fail with a 403 error.
