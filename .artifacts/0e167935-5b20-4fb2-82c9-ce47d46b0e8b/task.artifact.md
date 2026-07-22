# Tasks - Vertex AI & App Check Migration

- [ ] **Dependency Updates**
    - [x] Add `firebase-vertexai` and `firebase-appcheck` to `libs.versions.toml`
    - [x] Update `app/build.gradle.kts` with new dependencies
    - [ ] Remove legacy `generativeai` dependency
- [ ] **Application Setup**
    - [ ] Initialize App Check in `CarActivityLogApp.kt`
- [ ] **Code Migration**
    - [ ] Update `GeminiRepository.kt` to use `Firebase.vertexAI`
    - [ ] Remove API key usage from `GeminiRepository.kt`
    - [ ] Ensure `DiagnosisViewModel.kt` and `AddCarViewModel.kt` work with the new repository signature
- [ ] **Cleanup**
    - [ ] Remove `gemini_api_key` from `build.gradle.kts` and `local.properties`
- [ ] **Verification**
    - [ ] Verify build
    - [ ] Test AI features in Debug mode (using Debug Provider)
