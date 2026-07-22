# Implementation Plan - Car Title & Bug Fixes

Improve the visibility and usage of the "Car Title" field, fix PDF scanning issues, and refine country selection defaults.

## User Review Required

> [!NOTE]
> I will swap the display priority throughout the app. Instead of showing "Make Model" and falling back to "Nickname", I will show "Nickname" and fallback to "Make Model".

> [!IMPORTANT]
> Regarding PDF scanning: I've identified that the default model name fallback was likely incorrect (`gemini-3.5-flash-lite` doesn't exist) and the PDF prompt could be improved to handle multi-page documents better.

## Proposed Changes

### Domain Layer

#### [MODIFY] [Car.kt](file:///D:/Car%20Activity%20Log/app/src/main/java/com/dariusepure/caractivitylog/domain/Car.kt)
- Add `displayName` extension.
- Change default `plateCountry` from "RO" to "" to avoid forced defaults.

```kotlin
val Car.displayName: String
    get() = name.ifBlank { "$make $model".trim() }.ifBlank { "Unnamed car" }
```

### Data Layer

#### [MODIFY] [GeminiRepository.kt](file:///D:/Car%20Activity%20Log/app/src/main/java/com/dariusepure/caractivitylog/data/ai/GeminiRepository.kt)
- Fix the fallback model name to `gemini-1.5-flash`.
- Update the PDF scanning prompt to specifically mention multi-page documents.
- Enhance `extractJson` to handle potential AI chatter better.

### UI Layer - Add/Edit Car

#### [MODIFY] [AddCarScreen.kt](file:///D:/Car%20Activity%20Log/app/src/main/java/com/dariusepure/caractivitylog/ui/cars/AddCarScreen.kt)
- Move "Car Title" to the top.
- Remove the fallback to `europeanCountries[0]` when loading a car; let it be `null` if not found.
- Ensure the save button handles the empty country case correctly.

#### [MODIFY] [AddCarViewModel.kt](file:///D:/Car%20Activity%20Log/app/src/main/java/com/dariusepure/caractivitylog/ui/cars/AddCarViewModel.kt)
- Update validation to allow empty country if license plate is also empty.

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
- Verify code compilation.

### Manual Verification
1. **Title Priority**: Add a car with a title. Verify the title shows in the list.
2. **Empty Title**: Add a car without a title. Verify "Make Model" shows in the list.
3. **Country Default**: Open "Add Car". Verify country is blank. Load an existing car with no country set. Verify country remains blank.
4. **PDF Scan**: Try scanning a multi-page PDF document. Verify data extraction works more reliably.
