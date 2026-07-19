# Adăugare detalii motor și caroserie suplimentare

Acest plan detaliază pașii pentru a adăuga câmpuri tehnice avansate: numărul de supape per cilindru, numărul de uși și spațiul portbagajului, inclusiv calcularea automată a numărului total de supape.

## Schimbări propuse

### [Componenta Domain]
#### [MODIFY] [Car.kt](file:///home/darius/StudioProjects/car_activity_log/app/src/main/java/com/dariusepure/caractivitylog/domain/Car.kt)
- Adăugarea câmpurilor:
    - `valvesPerCylinder: Int`
    - `numberOfDoors: Int`
    - `bootSpace: Int` (în litri)

### [Componenta Data]
#### [MODIFY] [FirestoreCar.kt](file:///home/darius/StudioProjects/car_activity_log/app/src/main/java/com/dariusepure/caractivitylog/data/cars/FirestoreCar.kt)
- Adăugarea câmpurilor pentru persistența Firebase și actualizarea mapărilor `toFirebase` și `fromFirebase`.

### [Componenta UI]
#### [MODIFY] [AddCarViewModel.kt](file:///home/darius/StudioProjects/car_activity_log/app/src/main/java/com/dariusepure/caractivitylog/ui/cars/AddCarViewModel.kt)
- Actualizarea funcției `onAddOrUpdateCar` pentru a procesa noile câmpuri.

#### [MODIFY] [AddCarScreen.kt](file:///home/darius/StudioProjects/car_activity_log/app/src/main/java/com/dariusepure/caractivitylog/ui/cars/AddCarScreen.kt)
- Adăugarea input-urilor pentru:
    - **Valves per Cylinder** (în secțiunea Engine).
    - **Number of Doors** și **Boot Space (L)** (în secțiunea Dimensions & Chassis).
- Actualizarea stării locale și a logicii de salvare.

#### [MODIFY] [TechnicalSheetScreen.kt](file:///home/darius/StudioProjects/car_activity_log/app/src/main/java/com/dariusepure/caractivitylog/ui/cars/TechnicalSheetScreen.kt)
- Afișarea noilor câmpuri.
- **Calcul automat**: Afișarea numărului total de supape (ex: "16 (4 per cylinder)").

## Plan de verificare
- [ ] Verificarea calculului automat al supapelor (ex: 4 cilindri * 4 supape = 16).
- [ ] Verificarea persistenței noilor câmpuri (Doors, Boot Space).
- [ ] Testarea validării numerice pe noile input-uri.
