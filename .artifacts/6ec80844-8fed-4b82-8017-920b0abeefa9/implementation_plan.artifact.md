# Corecție Definitivă: Stocare Canonică și Conversie Reversibilă

Pentru a elimina definitiv driftul (170 -> 106 -> 171) și a satisface cerința "revii la valoarea inițială", vom adopta o strategie de **Stocare Canonică în Unități Metrice** (KM, KM/H) în baza de date, realizând conversia doar pentru afișare și intrare.

## Schimbări propuse

### [Componenta UI]
#### [MODIFY] [AddCarViewModel.kt](file:///home/darius/StudioProjects/car_activity_log/app/src/main/java/com/dariusepure/caractivitylog/ui/cars/AddCarViewModel.kt)
- **ELIMINARE**: Vom șterge logica de conversie "batch" a kilometrajului (`logs.forEach { ... }`) atunci când se schimbă țara. Datele din Firestore vor rămâne mereu în KM.
- **Salvare**: Funcția `onAddOrUpdateCar` va primi valorile așa cum sunt în UI și le va converti în Metric (KM/H, L, kg, mm) înainte de salvare, bazându-se pe unitatea țării selectate.

#### [MODIFY] [AddCarScreen.kt](file:///home/darius/StudioProjects/car_activity_log/app/src/main/java/com/dariusepure/caractivitylog/ui/cars/AddCarScreen.kt)
- **Încărcare**: Când se încarcă o mașină, valorile din DB (Metric) vor fi convertite în unitatea locală pentru afișare în `OutlinedTextField` (ex: KM/H -> MPH dacă mașina e din UK).
- **Interacțiune**: Când utilizatorul schimbă țara în dropdown, valorile din câmpurile de text se vor converti vizual pentru a reflecta noua unitate, permițând utilizatorului să vadă echivalentul instant.

#### [MODIFY] [TechnicalSheetScreen.kt](file:///home/darius/StudioProjects/car_activity_log/app/src/main/java/com/dariusepure/caractivitylog/ui/cars/TechnicalSheetScreen.kt)
- Afișarea va face conversia Metric -> Local bazat pe țara mașinii, folosind `roundToInt()` pentru prezentare.

#### [MODIFY] [MileageHistoryScreen.kt](file:///home/darius/StudioProjects/car_activity_log/app/src/main/java/com/dariusepure/caractivitylog/ui/cars/MileageHistoryScreen.kt)
- Istoricul va afișa log-urile (stocate ca KM) convertite în Mile dacă țara o cere.

#### [MODIFY] [CarFormatters.kt](file:///home/darius/StudioProjects/car_activity_log/app/src/main/java/com/dariusepure/caractivitylog/ui/common/CarFormatters.kt)
- Adăugarea de funcții utilitare pentru conversia metric/imperial cu precizie `Double` și rotunjire `.5 up` pentru UI.

## Plan de verificare
- [ ] **Test Reversibilitate**: Salvare 170 km/h (RO) -> Schimbare în UK (Display 106) -> Salvare -> Schimbare înapoi în RO -> Verificare Display 170.
- [ ] **Test Kilometraj**: Verificare dacă schimbarea țării nu mai modifică valorile brute în Firestore, ci doar cum sunt ele văzute în aplicație.
