# Fix Build Error: No parameter with name 'apiKey' found

The project is experiencing a build error in `GeminiRepository.kt` because the `GenerativeBackend.googleAI()` function in the Firebase AI Logic SDK does not accept an `apiKey` parameter.

## User Review Required

> [!IMPORTANT]
> The Firebase AI Logic SDK manages the Gemini API key automatically through the Firebase project configuration (typically via `google-services.json` and the Firebase Console). You should ensure that the Gemini Developer API is enabled in your Firebase project and connected to your app.

## Proposed Changes

### AI Logic Integration

#### [MODIFY] [GeminiRepository.kt](file:///D:/Car Activity Log/app/src/main/java/com/dariusepure/caractivitylog/data/ai/GeminiRepository.kt)

- Remove the `apiKey` argument from the `GenerativeBackend.googleAI()` call in the `generateCarImage` function.

```kotlin
// Change from:
backend = GenerativeBackend.googleAI(apiKey = context.getString(R.string.gemini_api_key))

// To:
backend = GenerativeBackend.googleAI()
```

## Verification Plan

### Automated Tests
- Run `./gradlew :app:compileDebugKotlin` to verify that the build error is resolved.

### Manual Verification
- Deploy the app and test the car image generation feature to ensure it still works correctly with the Firebase-managed API key.
