# Rezumatul Modificărilor: Gestionare Baterie pentru Mașini Electrice și Hibride

Am implementat logica pentru a gestiona capacitatea bateriei și afișarea condiționată a câmpurilor în funcție de tipul de combustibil selectat.

## Modificări principale

### 1. Model de Date (Domain & Data)
- Adăugat câmpul `batteryCapacity` (stocat ca `Double` pentru precizie) în clasele `Car` și `FirestoreCar`.
- Actualizat mappers-urile pentru a asigura sincronizarea corectă cu Firebase.

### 2. UI Inteligent (Add Car Screen)
- Am implementat afișarea condiționată în secțiunea "Engine & Performance":
    - **Electric**: Câmpul "Fuel Tank" este ascuns; apare doar "Battery Capacity (kWh)".
    - **Hybrid**: Apar ambele câmpuri ("Fuel Tank" și "Battery Capacity"), deoarece aceste mașini folosesc ambele resurse.
    - **Altele (Petrol, Diesel etc.)**: Apare doar câmpul "Fuel Tank Capacity (L)".

### 3. Afișare în Fișa Tehnică
- În ecranul "Technical Sheet", capacitatea bateriei este afișată acum sub tipul de combustibil, dacă valoarea este mai mare decât 0.

## Verificare
- [x] Proiectul compilează cu succes (`gradle assembleDebug`).
- [x] Testare vizuală: Schimbarea Fuel Type în "Electric" actualizează instantaneu input-urile disponibile.
- [x] Datele sunt persistate corect în Firestore și reîncărcate la editare.

> [!NOTE]
> Această schimbare face aplicația mult mai relevantă pentru posesorii de vehicule moderne (EV/PHEV), oferind exact câmpurile de care au nevoie.
