# Gestionare Condițională Capacitate Baterie și Rezervor

Acest plan detaliază adăugarea suportului pentru mașini electrice și hibride prin introducerea câmpului de capacitate baterie și afișarea lui condiționată în funcție de tipul de combustibil.

## Schimbări propuse

### [Componenta Domain]
#### [MODIFY] [Car.kt](file:///home/darius/StudioProjects/car_activity_log/app/src/main/java/com/dariusepure/caractivitylog/domain/Car.kt)
- Adăugarea câmpului `batteryCapacity: Double = 0.0`.

### [Componenta Data]
#### [MODIFY] [FirestoreCar.kt](file:///home/darius/StudioProjects/car_activity_log/app/src/main/java/com/dariusepure/caractivitylog/data/cars/FirestoreCar.kt)
- Adăugarea câmpului `batteryCapacity` și actualizarea funcțiilor de mapare.

### [Componenta UI]
#### [MODIFY] [AddCarViewModel.kt](file:///home/darius/StudioProjects/car_activity_log/app/src/main/java/com/dariusepure/caractivitylog/ui/cars/AddCarViewModel.kt)
- Actualizarea funcției `onAddOrUpdateCar` pentru a include noul câmp.

#### [MODIFY] [AddCarScreen.kt](file:///home/darius/StudioProjects/car_activity_log/app/src/main/java/com/dariusepure/caractivitylog/ui/cars/AddCarScreen.kt)
- Adăugarea stării locale `batteryCapacity`.
- Implementarea logicii de afișare condiționată în secțiunea "Engine & Performance":
    - Dacă Fuel Type == **Electric**: Afișează doar "Battery Capacity (kWh)".
    - Dacă Fuel Type == **Hybrid**: Afișează ambele ("Fuel Tank" și "Battery Capacity").
    - Altfel: Afișează doar "Fuel Tank Capacity (L)".

#### [MODIFY] [TechnicalSheetScreen.kt](file:///home/darius/StudioProjects/car_activity_log/app/src/main/java/com/dariusepure/caractivitylog/ui/cars/TechnicalSheetScreen.kt)
- Afișarea capacității bateriei în fișa tehnică dacă valoarea este mai mare decât zero.

## Plan de verificare
- [ ] Testare: Selectare "Electric" -> Verificare dacă "Fuel Tank" dispare și apare "Battery Capacity".
- [ ] Testare: Selectare "Hybrid" -> Verificare dacă apar ambele câmpuri.
- [ ] Verificare persistență: Salvare valoare baterie și verificare în "Technical Sheet".
