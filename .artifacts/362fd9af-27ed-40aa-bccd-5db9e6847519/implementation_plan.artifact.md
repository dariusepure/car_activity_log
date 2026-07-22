# Implementation Plan - Technical Sheet Expansion & Gemini Remote Config

This plan covers two main tasks:
1.  Expanding the **Technical Sheet** to show all car specifications.
2.  Integrating **Firebase Remote Config** to manage Gemini AI parameters (model name, timeout, etc.) dynamically.

## User Review Required

> [!IMPORTANT]
> - **Technical Sheet**: I will reorganize the layout to include all 40+ fields from the `Car` model.
> - **Gemini Remote Config**: To use this effectively, you will need to add the corresponding keys in the Firebase Console (e.g., `gemini_model_name`) after I implement the code.

## Proposed Changes

### Technical Sheet Expansion

#### [MODIFY] [TechnicalSheetScreen.kt](file:///D:/Car%20Activity%20Log/app/src/main/java/com/dariusepure/caractivitylog/ui/cars/TechnicalSheetScreen.kt)
- Reorganize specifications into 4 categories:
    - **Identity & Style**: Plate, VIN, Color, Country, etc.
    - **Engine & Performance**: Power, Torque, Fuel System, Engine Code, etc.
    - **Transmission & Chassis**: Gearbox, Drivetrain, Brakes, Suspension.
    - **Dimensions & Capacity**: All physical dimensions and tank/battery capacities.

### Gemini Remote Config Integration

#### [MODIFY] [AppModule.kt](file:///D:/Car%20Activity%20Log/app/src/main/java/com/dariusepure/caractivitylog/di/AppModule.kt)
- Add a provider for `FirebaseRemoteConfig`.
- Set up minimum fetch interval (e.g., 1 hour for production, 0 for debug).

#### [MODIFY] [GeminiRepository.kt](file:///D:/Car%20Activity%20Log/app/src/main/java/com/dariusepure/caractivitylog/data/ai/GeminiRepository.kt)
- Inject `FirebaseRemoteConfig`.
- Replace hardcoded `modelName` and `timeout` with values from Remote Config.
- Use `fetchAndActivate()` to ensure latest values are used.

## Verification Plan

### Automated Tests
- Verify code compilation.
- Ensure all `Car` fields are correctly mapped in the `TechnicalSheetScreen`.

### Manual Verification
1.  Open **Technical Sheet** and verify all car data is visible.
2.  Check **Logcat** for "Gemini using model: ..." to verify Remote Config integration (once keys are set in Firebase).
