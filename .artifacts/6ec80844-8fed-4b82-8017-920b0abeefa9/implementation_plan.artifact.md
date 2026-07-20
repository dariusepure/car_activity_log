# Implement Dynamic Battery/Fuel Fields based on Fuel Type

The user wants to adjust the car details input and display logic. Depending on the car's fuel type, the fields for capacity should change:
- **Electric**: Hide `fuelTankCapacity`, show `batteryCapacity`.
- **Hybrid**: Show both `fuelTankCapacity` and `batteryCapacity`.
- **Other (Petrol, Diesel, LPG)**: Show `fuelTankCapacity`, hide `batteryCapacity`.

## Proposed Changes

### Domain & Data Layer

#### [MODIFY] [Car.kt](file:///D:/Car%20Activity%20Log/app/src/main/java/com/dariusepure/caractivitylog/domain/Car.kt)
- Add `val batteryCapacity: Double = 0.0` to the `Car` data class.

#### [MODIFY] [FirestoreCar.kt](file:///D:/Car%20Activity%20Log/app/src/main/java/com/dariusepure/caractivitylog/data/cars/FirestoreCar.kt)
- Add `val batteryCapacity: Double = 0.0` to `FirestoreCar`.
- Update `toFirebase()` and `fromFirebase()` to include the new field.

### UI Layer

#### [MODIFY] [AddCarViewModel.kt](file:///D:/Car%20Activity%20Log/app/src/main/java/com/dariusepure/caractivitylog/ui/cars/AddCarViewModel.kt)
- Update `onAddOrUpdateCar` parameter list to include `batteryCapacity: String`.
- Update the `Car` object creation inside `onAddOrUpdateCar` to set `batteryCapacity`.

#### [MODIFY] [AddCarScreen.kt](file:///D:/Car%20Activity%20Log/app/src/main/java/com/dariusepure/caractivitylog/ui/cars/AddCarScreen.kt)
- Add a new state variable `var batteryCapacity by remember { mutableStateOf("") }`.
- Update `LaunchedEffect` to populate `batteryCapacity` when editing.
- In the UI, conditionally show `fuelTankCapacity` field if `fuelType` is NOT "Electric".
- In the UI, conditionally show `batteryCapacity` field if `fuelType` is "Electric" or "Hybrid".
- Update calls to `viewModel.onAddOrUpdateCar` to include `batteryCapacity`.

#### [MODIFY] [TechnicalSheetScreen.kt](file:///D:/Car%20Activity%20Log/app/src/main/java/com/dariusepure/caractivitylog/ui/cars/TechnicalSheetScreen.kt)
- Update the "Engine & Transmission" section to display `batteryCapacity` if it's greater than 0.
- Ensure `fuelTankCapacity` is only shown if it's relevant (already handled by `if (car.fuelTankCapacity > 0)`, but could be more explicit based on `fuelType`).

## Verification Plan

### Automated Tests
- Build the project to ensure no compilation errors.
- (Optional) Run existing instrumented tests if any.

### Manual Verification
1. Open the app and go to "Add Car".
2. Select "Electric" as fuel type:
   - Verify `Fuel Tank Capacity` disappears.
   - Verify `Battery Capacity` appears.
3. Select "Hybrid" as fuel type:
   - Verify both `Fuel Tank Capacity` and `Battery Capacity` are visible.
4. Select "Petrol" as fuel type:
   - Verify `Battery Capacity` disappears.
   - Verify `Fuel Tank Capacity` is visible.
5. Save a car with each type and verify the data is correctly displayed in the "Technical Sheet".
6. Edit a saved car and verify the values are correctly pre-filled.
