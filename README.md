# 🏎️ Car Activity Log
**The Ultimate Android Companion for Your Vehicle Management**

---

## 🌟 Key Features

### 🛠️ Comprehensive Vehicle Management
- **Detailed Profiles**: Add and edit cars with exhaustive technical specs:
  - **Make & Model** (Extensive pre-defined brand list + custom option)
  - **Body Type**: Saloon, SUV, Hatchback, MPV, etc.
  - **Dimensions**: Length, Width, and Height in millimeters.
  - **Drivetrain**: Support for FWD, RWD, AWD, and 4WD.
  - **Engine Specs**: Fuel type, tank capacity, engine code, and displacement (**cc**).
  - **Manufacturing Country**: Full dropdown selection.
- **Visual Identity**: Set a profile photo for each car.
  - *Smart Compression*: Images are automatically resized (max 1024px) and compressed (70% quality) for high performance and low storage usage.
- **Smart Validation**: License plate validation using country-specific **Regex patterns** for over 40 European countries.

### 📊 Monitoring & History
- **Mileage Log**: Keep a chronological record of your car's distance traveled.
- **Vehicle Inspection (ITP)**: Manage technical inspection validity.
  - **Automatic Expiry Calculation**: Enter the date and duration; the app does the rest.
  - **Visual Alerts**: Expiration dates turn **RED** to warn you of expired documents.
  - **Auto-Sync**: Entering inspection mileage automatically updates the general mileage history.

### 🌗 Premium UI/UX Experience
- **Adaptive Theming**: Seamless switching between **Light Mode** and **True Black Dark Mode** (optimized for OLED screens).
- **Material 3 Design**: A modern, clean, and airy interface following Google's latest design guidelines.
- **Global Theme Sync**: Theme changes are applied instantly across the entire application.

### 📶 Connectivity & Reliability
- **Offline-First Architecture**: The app works perfectly without an internet connection.
- **Background Sync**: Changes are saved locally on the device and automatically synchronized with the **Firebase Cloud** once connection is restored.
- **Sync Status Indicators**: Real-time visual feedback (🔄 for pending sync, ✅ for synchronized data).

---

## 🛠️ Tech Stack

| Category | Technology |
| :--- | :--- |
| **Language** | **Kotlin** (Modern & Concise) |
| **UI Framework** | **Jetpack Compose** (Declarative UI) |
| **Architecture** | **MVVM** with strict Clean Architecture principles |
| **Dependency Injection** | **Hilt** (Dagger based) |
| **Database** | **Cloud Firestore** (with Local Persistence enabled) |
| **Cloud Storage** | **Firebase Storage** (for high-performance image hosting) |
| **Networking** | Firebase Auth & Firestore Real-time Listeners |
| **Image Loading** | **Coil** (Efficient async loading & caching) |
| **Concurrency** | Kotlin Coroutines & Flow |

---

> [!TIP]
> This project was developed with a focus on code maintainability and user efficiency, ensuring that all data processing is decoupled from the UI layer for a smooth and fast experience.

---
*Developed to simplify car ownership and provide total control over your vehicle's health and documentation.*
