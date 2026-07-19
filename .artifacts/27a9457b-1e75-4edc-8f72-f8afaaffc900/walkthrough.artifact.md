# Walkthrough - Adăugare Specificații: Rezervor și Tracțiune

Am extins baza de date și interfața aplicației pentru a include capacitatea rezervorului și tipul de tracțiune.

## Schimbări efectuate

### [Domain & Data Models]
- **[Car.kt](file:///D:/Car Activity Log/app/src/main/java/com/dariusepure/caractivitylog/domain/Car.kt)**: Am adăugat `fuelTankCapacity` (Double) și `drivetrain` (String).
- **[FirestoreCar.kt](file:///D:/Car Activity Log/app/src/main/java/com/dariusepure/caractivitylog/data/cars/FirestoreCar.kt)**: Am actualizat modelul de date pentru Firebase pentru a suporta noile câmpuri.

### [User Interface]
- **[AddCarScreen.kt](file:///D:/Car Activity Log/app/src/main/java/com/dariusepure/caractivitylog/ui/cars/AddCarScreen.kt)**:
    - Am adăugat un câmp de input pentru **Fuel Tank Capacity (L)** care acceptă numere zecimale.
    - Am implementat un selector (dropdown) pentru **Drivetrain** cu opțiunile predefinite: **FWD, RWD, AWD, 4WD**.
- **[CarDetailsScreen.kt](file:///D:/Car Activity Log/app/src/main/java/com/dariusepure/caractivitylog/ui/cars/CarDetailsScreen.kt)**: Noile specificații sunt acum vizibile în lista de detalii a mașinii.

## Verificare
- Compilarea proiectului a fost realizată cu succes.
- Datele sunt salvate corect în Firebase și persistă după restartarea aplicației.

> [!TIP]
> Capacitatea rezervorului este afișată în litri (L), iar tracțiunea folosește acronimele standard din industrie pentru o identificare rapidă.
