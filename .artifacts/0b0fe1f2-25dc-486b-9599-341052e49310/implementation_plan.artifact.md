# Fix Build Error: Missing Release Keystore File

The project fails to build because the `release` signing configuration expects a keystore file (`darius1702`) that is missing or incorrectly path-referenced in `local.properties`.

## User Review Required

> [!IMPORTANT]
> The release build is currently configured to use a signing key that doesn't exist at the specified path. I will modify the build script to handle this gracefully by falling back to debug signing if the release key is missing. This will allow the build to succeed, but the resulting APK/AAB won't be signed with your production key until the keystore file is provided and correctly configured in `local.properties`.

## Proposed Changes

### [app] Component

#### [MODIFY] [build.gradle.kts](file:///D:/Car%20Activity%20Log/app/build.gradle.kts)
- Update `signingConfigs` to check for the existence of the keystore file and presence of all required properties (`RELEASE_KEYSTORE_PATH`, `RELEASE_KEYSTORE_PASSWORD`, `RELEASE_KEYSTORE_ALIAS`, `RELEASE_ALIAS_PASSWORD`).
- Update `buildTypes.release` to use the `release` signing config only if it is fully configured, otherwise fall back to the `debug` signing configuration.

## Verification Plan

### Automated Tests
- Run `./gradlew :app:assembleRelease` to verify that the build now completes successfully (falling back to debug signing).

### Manual Verification
- Check the build output to ensure no "property 'storeFile' specifies file ... which doesn't exist" errors occur.
