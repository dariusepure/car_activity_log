# Implementation Plan - Modern AI Diagnosis Chat UI

The user wants to improve the AI Diagnosis Chat experience by making the input field rise with the keyboard, modernizing the chat UI, and fixing the raw display of markdown characters (like `***`).

## Proposed Changes

### [UI Layer]

#### [MODIFY] [DiagnosisScreen.kt](file:///D:/Car Activity Log/app/src/main/java/com/dariusepure/caractivitylog/ui/cars/DiagnosisScreen.kt)
- **Keyboard Handling**: Add `Modifier.imePadding()` to the bottom input container and ensure the `Column` inside `Scaffold` correctly handles window insets.
- **Modern UI**:
    - Redesign `ChatBubble` with more refined shapes and softer background colors.
    - Improve the `TypingIndicator` with a smoother look.
    - Refactor the Input Area to use a rounded `TextField` and a more integrated Send button.
- **Markdown Rendering**:
    - Implement a `MarkdownText` helper function (using `buildAnnotatedString`) to handle basic formatting like **bold** (parsing `**text**`) and cleaning up triple asterisks (`***`) often used by AI models for horizontal rules or emphasis.

## Verification Plan

### Manual Verification
- Deploy to emulator/device.
- Open AI Diagnosis Chat.
- Click the input field and verify it moves up above the keyboard.
- Send a message and observe the new bubble style.
- Wait for AI response and verify that formatting (like bold text) is rendered correctly instead of showing raw `**` or `***` characters.
