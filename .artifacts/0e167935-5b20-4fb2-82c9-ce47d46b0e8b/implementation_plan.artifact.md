# Implementation Plan - Car Title Refinement

Improve the visibility and usage of the "Car Title" field in the Add/Edit car screens, and ensure it falls back to "Make + Model" when not provided, prioritizing it throughout the app.

## User Review Required

> [!NOTE]
> I will swap the display priority throughout the app. Instead of showing "Make Model" and falling back to "Nickname", I will show "Nickname" and fallback to "Make Model". This aligns with your request to add a Car Title.

## Proposed Changes

### Domain Layer

#### [MODIFY] [Car.kt](file:///D:/Car%20Activity%20Log/app/src/main/java/com/dariusepure/caractivitylog/domain/Car.kt)
Add a `displayName` extension property to the `Car` data class (or just a helper function) to centralize the naming logic.

```kotlin
val Car.displayName: String
    get() = name.ifBlank { "$make $model".trim() }.ifBlank { "Unnamed car" }
```

### UI Layer - Add/Edit Car

#### [MODIFY] [AddCarScreen.kt](file:///D:/Car%20Activity%20Log/app/src/main/java/com/dariusepure/caractivitylog/ui/cars/AddCarScreen.kt)
- Move the "Car Title" field to the very top of the "Identity & Style" section.
- Add a placeholder that dynamically shows the current `make + model` if the title is empty.

#### [MODIFY] [AddCarViewModel.kt](file:///D:/Car%20Activity%20Log/app/src/main/java/com/dariusepure/caractivitylog/ui/cars/AddCarViewModel.kt)
- (Optional) Could auto-populate the `name` field if it's empty when saving, but using the `displayName` extension is cleaner for display purposes. I will stick to the extension approach to keep the saved data "pure" (blank name means use default).

### UI Layer - Display

#### [MODIFY] [CarListScreen.kt](file:///D:/Car%20Activity%20Log/app/src/main/java/com/dariusepure/caractivitylog/ui/cars/CarListScreen.kt)
Update `CarCard` to use `car.displayName`.

#### [MODIFY] [CarDetailsScreen.kt](file:///D:/Car%20Activity%20Log/app/src/main/java/com/dariusepure/caractivitylog/ui/cars/CarDetailsScreen.kt)
Update the header to use `car.displayName`.

#### [MODIFY] [DiagnosisViewModel.kt](file:///D:/Car%20Activity%20Log/app/src/main/java/com/dariusepure/caractivitylog/ui/cars/DiagnosisViewModel.kt)
Update the AI context and `carName` state to use `car.displayName`.

#### [MODIFY] [InspectionHistoryScreen.kt](file:///D:/Car%20Activity%20Log/app/src/main/java/com/dariusepure/caractivitylog/ui/cars/InspectionHistoryScreen.kt)
#### [MODIFY] [TechnicalSheetScreen.kt](file:///D:/Car%20Activity%20Log/app/src/main/java/com/dariusepure/caractivitylog/ui/cars/TechnicalSheetScreen.kt)
Update to use `car.displayName`.

## Verification Plan

### Automated Tests
- I will verify that the code compiles after the changes.

### Manual Verification
1. Open the "Add Car" screen.
2. Verify "Car Title" is at the top.
3. Enter "Toyota" as Make and "Corolla" as Model.
4. Leave "Car Title" empty and save.
5. Verify the list shows "Toyota Corolla".
6. Edit the car and set "Car Title" to "The Daily".
7. Save and verify the list shows "The Daily".
