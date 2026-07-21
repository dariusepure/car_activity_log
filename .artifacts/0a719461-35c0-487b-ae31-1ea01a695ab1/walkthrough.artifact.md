# Walkthrough - Split Scanning: Photo and PDF

I have implemented the requested split scanning feature, allowing you to scan registration certificates using either a photo or a PDF file. This functionality is now available in both the "Add Car" and "Edit Car" screens.

## Changes

### [Component: Data & AI]

#### [GeminiRepository.kt](file:///D:/Car Activity Log/app/src/main/java/com/dariusepure/caractivitylog/data/ai/GeminiRepository.kt)
- Added `scanDocument(uri: Uri, mimeType: String)` to handle generic file data (like PDFs) using `blob` parts.
- Switched to `gemini-1.5-flash` model which provides better support for multi-modal inputs including PDFs.
- Updated both scanning and diagnosis to use the `gemini-1.5-flash` model.

### [Component: UI - Add/Edit Car]

#### [AddCarViewModel.kt](file:///D:/Car Activity Log/app/src/main/java/com/dariusepure/caractivitylog/ui/cars/AddCarViewModel.kt)
- Added `scanDocument(uri: Uri, mimeType: String)` to bridge the UI with the repository.

#### [AddCarScreen.kt](file:///D:/Car Activity Log/app/src/main/java/com/dariusepure/caractivitylog/ui/cars/AddCarScreen.kt)
- Replaced the single scan button with two buttons: **"Scan Photo"** and **"Scan PDF"**.
- Added `photoPicker` for image selection (`image/*`).
- Added `pdfPicker` for PDF selection (`application/pdf`).
- Enabled these buttons in both "Add Car" and "Edit Car" modes (previously only available in "Add Car").

---

## Verification Plan

### Automated Tests
- Build sync and compilation verify that the new `GeminiRepository` methods and UI pickers are correctly integrated.

### Manual Verification
- **Add Car**:
    - Verify "Scan Photo" opens the gallery and populates fields from an image.
    - Verify "Scan PDF" opens the file picker and populates fields from a PDF.
- **Edit Car**:
    - Navigate to an existing car and verify both scan buttons are present and functional.
