# Fix Run Configurations

The goal is to replace the existing, incorrect "app" run configuration with a proper Android Run Configuration targeting the `:app` module and launching the `MainActivity`.

## Proposed Changes

### IDE Configurations

#### [NEW] [app.xml](file:///Users/taskmaster/abiapay-pos-kotlin/.idea/runConfigurations/app.xml)
Create a new shared Android Run Configuration. This configuration will:
- Target the `:app` module.
- Launch the default activity (`MainActivity`).
- Use the default deployment target (connected device or emulator).

#### [MODIFY] [workspace.xml](file:///Users/taskmaster/abiapay-pos-kotlin/.idea/workspace.xml)
Remove the existing, non-functional "app" `Application` configuration to avoid duplication and confusion.

## Verification Plan

### Manual Verification
- The user should check the Run Configuration dropdown in Android Studio.
- There should be a new entry named "app" with the Android "green robot" icon.
- Selecting this configuration and clicking "Run" should build the APK and launch it on a device/emulator.
