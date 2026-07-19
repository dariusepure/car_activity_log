# Reparare Dark Mode și Optimizare Vizibilitate

Utilizatorul dorește ca Dark Mode să funcționeze corect, cu un fundal complet negru și text alb, asigurându-se în același timp că elementele de sistem (ora, bateria) rămân vizibile. De asemenea, toggle-ul (soare/lună) trebuie să controleze tema la nivelul întregii aplicații.

## Proposed Changes

### [Theme & Styling]

#### [MODIFY] [Color.kt](file:///D:/Car Activity Log/app/src/main/java/com/dariusepure/caractivitylog/ui/theme/Color.kt)
- Schimbarea `GarageMidnight` la `Color.Black` (`0xFF000000`) pentru un fundal "true black" în Dark Mode.
- Asigurarea că `GarageWhite` este folosit consecvent pentru text.

#### [MODIFY] [Theme.kt](file:///D:/Car Activity Log/app/src/main/java/com/dariusepure/caractivitylog/ui/theme/Theme.kt)
- Adăugarea logicii `SideEffect` pentru a configura culorile barelor de sistem (status bar, navigation bar).
- Asigurarea vizibilității orei și bateriei prin setarea corectă a `appearanceLightStatusBars`.

### [Theme Management]

#### [MODIFY] [ThemeViewModel.kt](file:///D:/Car Activity Log/app/src/main/java/com/dariusepure/caractivitylog/ui/theme/ThemeViewModel.kt)
- Adăugarea persistenței setării de temă folosind `SharedPreferences`.

#### [MODIFY] [MainActivity.kt](file:///D:/Car Activity Log/app/src/main/java/com/dariusepure/caractivitylog/MainActivity.kt)
- Injectarea `ThemeViewModel` și observarea stării `isDarkMode`.
- Aplicarea temei global și utilizarea `enableEdgeToEdge()`.

#### [MODIFY] [CarListScreen.kt](file:///D:/Car Activity Log/app/src/main/java/com/dariusepure/caractivitylog/ui/cars/CarListScreen.kt)
- Utilizarea `ThemeViewModel` la nivel de activitate pentru a asigura sincronizarea toggle-ului.

## Verification Plan

### Manual Verification
- Comutarea între Dark și Light mode folosind butonul din `CarListScreen`.
- Verificarea fundalului (trebuie să fie negru complet în Dark Mode).
- Verificarea vizibilității ceasului și bateriei în ambele moduri.
- Repornirea aplicației pentru a verifica dacă setarea se păstrează.
