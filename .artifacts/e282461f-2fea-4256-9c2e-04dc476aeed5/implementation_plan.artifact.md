# Fix Diagnosis Crash and Adjust Chat Bar UI

The user reports a crash when opening the Diagnosis screen and requests that the chat bar be lifted when the keyboard is visible.

## User Review Required

> [!IMPORTANT]
> I noticed that the Kotlin version in the project is set to `2.4.10`, which is likely a typo or an unstable version causing binary incompatibilities with libraries like Ktor (used by Gemini). I propose changing it to `2.0.21`, which is the current stable version for Kotlin 2.0.

## Proposed Changes

### Dependencies & Configuration

#### [MODIFY] [libs.versions.toml](file:///D:/Car%20Activity%20Log/gradle/libs.versions.toml)
- Update Kotlin version from `2.4.10` to `2.0.21`.
- Add explicit Ktor dependencies to ensure `HttpTimeout` and other plugins are correctly resolved.

#### [MODIFY] [app/build.gradle.kts](file:///D:/Car%20Activity%20Log/app/build.gradle.kts)
- Add Ktor dependencies.
- Remove or update the `resolutionStrategy` that forces an unusual Kotlin version.

### Diagnosis Screen UI

#### [MODIFY] [DiagnosisScreen.kt](file:///D:/Car%20Activity%20Log/app/src/main/java/com/dariusepure/caractivitylog/ui/cars/DiagnosisScreen.kt)
- Add `Modifier.imePadding()` to the chat input area to ensure it lifts when the keyboard opens.
- Ensure the `LazyColumn` scrolls to the bottom when the keyboard appears.

## Verification Plan

### Automated Tests
- Build the project to ensure dependency resolution is successful.
- Run the app and navigate to the Diagnosis screen to verify the crash is fixed.

### Manual Verification
- Open the Diagnosis screen.
- Tap the "Describe the problem..." text field.
- Verify that the input bar moves up above the keyboard.
- Verify that the chat messages are still visible and the list scrolls correctly.
