# 🏎️ Car Activity Log
**The Ultimate Android Companion for Your Vehicle Management with AI Integration**

---

## 🌟 Key Features

### 🤖 AI-Powered Intelligence
- **Smart Diagnosis**: Persistent chat with an expert AI car mechanic.
  - **History Persistence**: Conversations are saved per car and synced to the cloud.
  - **Context-Aware**: The AI knows your car's specs (make, model, year, engine) to provide precise advice.
  - **Reset Option**: Clear conversation history whenever you need a fresh start.
- **AI Document Scanning**: Extract technical data instantly from registration certificates.
  - **Dual Input**: Support for both **Photo** (Gallery/Camera) and **PDF** files.
  - **High Accuracy**: Powered by Google's Gemini Pro Vision / Flash models.
  - **Direct Population**: Automatically fills fields like VIN, Make, Model, Year, Fuel Type, Power, and more.

### 🛠️ Comprehensive Vehicle Management
- **Exhaustive Profiles**: Detailed technical specs for every vehicle:
  - **Make & Model**: Quick selection from 70+ brands or manual input.
  - **Body & Chassis**: Saloon, SUV, Hatchback, etc., plus Drivetrain (FWD, RWD, AWD) and Gearbox/Gears.
  - **Dimensions**: Precise measurements (Length, Width, Height) and Weight.
  - **Engine & Performance**: Engine code, layout, aspiration, displacement (cc), Power (hp/kW), and Torque.
- **Visual Identity**: Support for car profile photos with smart compression (max 1024px, 70% quality).
- **Smart Validation**: License plate validation using country-specific **Regex patterns** for 40+ European countries.

### 📊 Monitoring & History
- **Mileage Log**: Chronological distance tracking for every vehicle.
- **Vehicle Inspection (ITP)**:
  - **Auto-Expiry**: Automated calculation of the next inspection date.
  - **Visual Alerts**: Red warnings for expired or near-expiry documents.
  - **Auto-History**: Inspection mileage is automatically logged into the general mileage history.

### 🌗 Premium UI/UX
- **Adaptive Theming**: Light Mode and **OLED Black Dark Mode**.
- **Material 3**: Modern, clean interface following the latest Google design standards.
- **Global Sync**: Instant theme application across all screens.

---

## 🛠️ Tech Stack

| Category | Technology |
| :--- | :--- |
| **Language** | **Kotlin** (1.9+) |
| **UI Framework** | **Jetpack Compose** (Declarative UI) |
| **AI SDK** | **Google AI (Gemini 1.5 Flash)** |
| **Architecture** | **MVVM** + Clean Architecture |
| **DI** | **Hilt** (Dagger) |
| **Networking** | **Ktor Client** (with HttpTimeout & Logging) |
| **Database** | **Cloud Firestore** (Real-time & Offline Persistence) |
| **Storage** | **Firebase Storage** (Media hosting) |
| **Serialization** | **Kotlinx Serialization** |
| **Image Loading** | **Coil** |

---

## ⚙️ Configuration & Setup

### API Keys
To use the AI features, you must provide a Gemini API Key in your `local.properties` file:
```properties
gemini.api.key=YOUR_API_KEY_HERE
```
*Note: `local.properties` is automatically ignored by Git to keep your keys safe.*

### Firebase Structure
The app uses a hierarchical Firestore structure:
- `users/{uid}/cars/{carId}`
  - `/mileage/{logId}`
  - `/inspections/{inspectionId}`
  - `/diagnosis/{messageId}`

---

> [!IMPORTANT]
> This app is designed to be **Offline-First**. All changes are saved locally and synced automatically when a connection is available. Look for the ✅ (Synced) or 🔄 (Pending) indicators.

---
*Developed to provide total control over your vehicle's health and documentation with the power of AI.*
