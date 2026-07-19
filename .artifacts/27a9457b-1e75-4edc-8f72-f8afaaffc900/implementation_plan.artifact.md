# Adăugare Câmpuri: Capacitate Rezervor și Tracțiune

Utilizatorul dorește extinderea specificațiilor mașinii cu două câmpuri noi:
1.  **Capacitate Rezervor**: Volumul de combustibil în litri.
2.  **Tracțiune (Drivetrain)**: Tipul de tracțiune (FWD, RWD, AWD, 4WD), selectabil dintr-o listă.

## Proposed Changes

### [Domain & Data]

#### [MODIFY] [Car.kt](file:///D:/Car Activity Log/app/src/main/java/com/dariusepure/caractivitylog/domain/Car.kt)
- Adăugarea câmpurilor: `fuelTankCapacity` (Int/Double) și `drivetrain` (String).

#### [MODIFY] [FirestoreCar.kt](file:///D:/Car Activity Log/app/src/main/java/com/dariusepure/caractivitylog/data/cars/FirestoreCar.kt)
- Adăugarea noilor câmpuri și actualizarea metodelor de conversie pentru Firebase.

### [UI Logic]

#### [MODIFY] [AddCarViewModel.kt](file:///D:/Car Activity Log/app/src/main/java/com/dariusepure/caractivitylog/ui/cars/AddCarViewModel.kt)
- Actualizarea funcției `onAddOrUpdateCar` pentru a procesa noile date.

#### [MODIFY] [AddCarScreen.kt](file:///D:/Car Activity Log/app/src/main/java/com/dariusepure/caractivitylog/ui/cars/AddCarScreen.kt)
- Adăugarea unui câmp numeric pentru `Fuel Tank Capacity`.
- Adăugarea unui selector (Dropdown) pentru `Drivetrain` cu opțiunile: **FWD** (Front-Wheel Drive), **RWD** (Rear-Wheel Drive), **AWD** (All-Wheel Drive), **4WD** (Four-Wheel Drive).

#### [MODIFY] [CarDetailsScreen.kt](file:///D:/Car Activity Log/app/src/main/java/com/dariusepure/caractivitylog/ui/cars/CarDetailsScreen.kt)
- Afișarea noilor specificații în ecranul de detalii.

## Verification Plan

### Manual Verification
- Verificarea salvării corecte a noilor câmpuri.
- Verificarea afișării acestora în ecranul de detalii.
- Verificarea persistenței după repornirea aplicației.
