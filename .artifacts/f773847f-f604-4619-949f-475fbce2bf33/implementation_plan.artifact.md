# Plan de Implementare: Meniu Diagnoză AI Chat

Acest plan detaliază pașii pentru a adăuga o secțiune de "Diagnoză" unde utilizatorul poate discuta cu un AI despre starea tehnică a mașinii sale, primind sfaturi personalizate bazate pe datele vehiculului.

## User Review Required

> [!IMPORTANT]
> Chat-ul va fi alimentat de Gemini 1.5 Flash via Vertex AI for Firebase. Acesta va primi ca context datele tehnice ale mașinii (marcă, model, motorizare, VIN) și istoricul de service/mileage (dacă este disponibil) pentru a oferi răspunsuri cât mai precise.

## Proposed Changes

### 1. Navigare & Rute

#### [MODIFY] [AppNavigation.kt](file:///D:/Car%20Activity%20Log/app/src/main/java/com/dariusepure/caractivitylog/ui/AppNavigation.kt)
*   Adăugare `Screen.Diagnosis` cu ruta `"diagnosis/{carId}"`.
*   Configurare `composable` pentru noul ecran.

### 2. Interfață Detalii Mașină

#### [MODIFY] [CarDetailsScreen.kt](file:///D:/Car%20Activity%20Log/app/src/main/java/com/dariusepure/caractivitylog/ui/cars/CarDetailsScreen.kt)
*   Adăugare un nou `Card` în `LazyColumn` pentru "AI Diagnosis Chat".
*   Iconiță sugerată: `Icons.Default.Chat` sau `Icons.Default.Psychology`.

### 3. Layer de Date (AI Chat)

#### [MODIFY] [GeminiRepository.kt](file:///D:/Car%20Activity%20Log/app/src/main/java/com/dariusepure/caractivitylog/data/ai/GeminiRepository.kt)
*   Implementare funcție `startChat(carContext: String): ChatSession`.
*   AI-ul va fi instruit să acționeze ca un expert mecanic auto.

### 4. Logică & Ecran Chat (Nou)

#### [NEW] [DiagnosisScreen.kt](file:///D:/Car%20Activity%20Log/app/src/main/java/com/dariusepure/caractivitylog/ui/cars/DiagnosisScreen.kt)
*   Interfață stil chat (listă de mesaje, câmp de input jos).
*   Afișare indicator "AI is typing..." în timpul procesării.

#### [NEW] [DiagnosisViewModel.kt](file:///D:/Car%20Activity%20Log/app/src/main/java/com/dariusepure/caractivitylog/ui/cars/DiagnosisViewModel.kt)
*   Gestiune istoric mesaje.
*   Preluare date mașină din `CarRepository` pentru a crea contextul inițial al AI-ului.

## Verification Plan

### Manual Verification
1.  Navigare la detaliile unei mașini.
2.  Apăsare pe butonul "Diagnosis".
3.  Inițierea unui chat (ex: "Ce ulei îmi recomanzi pentru motorul meu?" sau "De ce se aude un sunet ciudat la roata dreaptă?").
4.  Verificarea faptului că AI-ul răspunde cunoscând detaliile mașinii (ex: confirmă tipul de motor).
5.  Testarea persistenței temporare a chat-ului pe durata sesiunii.
