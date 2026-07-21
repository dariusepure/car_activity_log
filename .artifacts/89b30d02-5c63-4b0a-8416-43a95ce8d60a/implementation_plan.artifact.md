# Implementation Plan - Simple AI Diagnosis Chat (Keyboard Focused)

The user wants to rebuild the Diagnosis feature from scratch, focusing ONLY on a high-quality chat experience with Gemini, without any complex charts or graphics. The main priority is perfect keyboard handling.

## User Review Required

> [!IMPORTANT]
> I will remove the health charts and focus entirely on a clean, responsive chat interface.
> The keyboard handling will be implemented to ensure the input field is never hidden by the keyboard and the list scrolls correctly.

## Proposed Changes

### 1. Data Models
#### [MODIFY] [DiagnosisState.kt](file:///D:/Car Activity Log/app/src/main/java/com/dariusepure/caractivitylog/ui/cars/DiagnosisState.kt)
- Reset to a simple message list and loading state.

### 2. Logic
#### [MODIFY] [DiagnosisViewModel.kt](file:///D:/Car Activity Log/app/src/main/java/com/dariusepure/caractivitylog/ui/cars/DiagnosisViewModel.kt)
- Simplified message sending logic.
- Proper handling of the welcome message and Gemini protocol.

### 3. UI Implementation
#### [MODIFY] [DiagnosisScreen.kt](file:///D:/Car Activity Log/app/src/main/java/com/dariusepure/caractivitylog/ui/cars/DiagnosisScreen.kt)
- **Top Section**: Just the TopAppBar.
- **Middle Section**: `LazyColumn` for messages with auto-scroll.
- **Bottom Section**: Input bar with `Modifier.imePadding()` to ensure it stays above the keyboard.
- No more charts or gauges.

### 4. AI Repository
#### [MODIFY] [GeminiRepository.kt](file:///D:/Car Activity Log/app/src/main/java/com/dariusepure/caractivitylog/data/ai/GeminiRepository.kt)
- Ensure `gemini-3.1-flash-lite` is used.
- Fix the history protocol to prevent "API Error".

## Verification Plan
1. Open chat.
2. Tap input - verify it rises correctly.
3. Send message - verify AI responds.
4. Verify auto-scroll to the latest message.
