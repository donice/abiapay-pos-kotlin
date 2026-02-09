# Hydrogen Bridge App

A simple Kotlin Android app that loads your web app in a WebView and provides a bridge to the Hydrogen Neo payment system.

## Features

- 🌐 Loads your web app in a full-screen WebView
- 💳 Integrates with Hydrogen Neo App for payments
- 🔗 JavaScript bridge for communication between web and native
- 📱 Supports all Hydrogen payment methods:
  - Card Payment
  - BreezePay
  - InstantPay/Transfer
  - Transaction History

## Prerequisites

### Required Software

1. **Java Development Kit (JDK) 11 or higher**
   ```bash
   java -version
   ```

2. **Android SDK** (via Android Studio or command-line tools)
   - Install Android Studio: https://developer.android.com/studio
   - Or install command-line tools: https://developer.android.com/studio#command-tools

3. **VS Code Extensions**
   - Extension Pack for Java
   - Kotlin Language
   - Gradle for Java

### Setup Steps

1. **Set ANDROID_HOME environment variable**
   
   Linux/Mac:
   ```bash
   export ANDROID_HOME=$HOME/Android/Sdk
   export PATH=$PATH:$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools
   ```
   
   Windows:
   ```
   setx ANDROID_HOME "C:\Users\YourUsername\AppData\Local\Android\Sdk"
   ```

2. **Open project in VS Code**
   ```bash
   cd HydrogenBridgeApp
   code .
   ```

## Configuration

### 1. Update Web App URL

Edit `app/src/main/java/com/yourcompany/hydrogenbridgeapp/MainActivity.kt`:

```kotlin
// Line ~27: Replace with your web app URL
webView.loadUrl("https://your-web-app-url.com")
```

### 2. Change Package Name (Optional)

If you want to change the package name from `com.yourcompany.hydrogenbridgeapp`:

1. Update in `app/build.gradle.kts`:
   ```kotlin
   namespace = "com.yournewpackage.name"
   applicationId = "com.yournewpackage.name"
   ```

2. Update in `app/src/main/AndroidManifest.xml`

3. Rename the folder structure and update the package declaration in MainActivity.kt

### 3. Update App Name

Edit `app/src/main/res/values/strings.xml`:
```xml
<string name="app_name">Your App Name</string>
```

## Building the App

### Using VS Code Tasks (Recommended)

1. Press `Ctrl+Shift+P` (or `Cmd+Shift+P` on Mac)
2. Type "Tasks: Run Task"
3. Select one of:
   - **Build Debug APK** - Build development version
   - **Build Release APK** - Build production version
   - **Clean Project** - Clean build files
   - **Install Debug APK** - Build and install to connected device

### Using Terminal

```bash
# Make gradlew executable (Linux/Mac)
chmod +x gradlew

# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Install to connected device
./gradlew installDebug

# Clean project
./gradlew clean
```

### Using Gradle Wrapper (Windows)

```cmd
gradlew.bat assembleDebug
gradlew.bat assembleRelease
```

## Finding Your APK

After building, find your APK at:

- **Debug**: `app/build/outputs/apk/debug/app-debug.apk`
- **Release**: `app/build/outputs/apk/release/app-release.apk`

## Installing on Device

### Via USB

1. Enable Developer Options on your Android device
2. Enable USB Debugging
3. Connect device via USB
4. Run: `./gradlew installDebug`

### Via APK File

1. Build the APK
2. Transfer `app-debug.apk` to your device
3. Install it (you may need to enable "Install from Unknown Sources")

## Web App Integration

### Add JavaScript Bridge to Your Web App

Include this code in your web app (see `web-integration-example.js` for full example):

```javascript
// Check if running in Android app
function isAndroidBridge() {
    return typeof HydrogenBridge !== 'undefined';
}

// Initiate payment
function processPayment(amount) {
    if (isAndroidBridge()) {
        // Amount in smallest unit (e.g., 5000 = ₦50.00)
        HydrogenBridge.initiateCardPayment(amount);
    }
}

// Handle result (REQUIRED)
function handleHydrogenPaymentResult(result) {
    if (result.status === 'SUCCESS') {
        console.log('Payment successful!', result.data);
        // Update your UI
    } else if (result.status === 'CANCELLED') {
        console.log('Payment cancelled');
    } else if (result.status === 'FAILED') {
        console.log('Payment failed:', result.data);
    }
}
```

### Available Bridge Methods

```javascript
// Card payment
HydrogenBridge.initiateCardPayment(amount)

// BreezePay
HydrogenBridge.initiateBreezePay(amount)

// Transfer/InstantPay
HydrogenBridge.initiateTransfer(amount)

// Transaction history
HydrogenBridge.showTransactionHistory()

// Check if Hydrogen app is installed
HydrogenBridge.isHydrogenAppInstalled()
```

## Testing

1. Install Hydrogen Neo App on your test device first
2. Install your bridge app
3. Open your bridge app - it will load your web app
4. Test payment functionality from your web app

## Troubleshooting

### Gradle Build Issues

```bash
# Clean and rebuild
./gradlew clean
./gradlew build --refresh-dependencies
```

### WebView Not Loading

- Check internet permissions in AndroidManifest.xml
- Verify your web app URL is correct
- Check network security config for HTTPS

### Hydrogen App Not Found

- Ensure Hydrogen Neo App is installed on the device
- Check the Intent action strings match Hydrogen's documentation

### JavaScript Bridge Not Working

- Ensure JavaScript is enabled in WebView settings
- Check that `handleHydrogenPaymentResult` function exists in your web app
- Look for errors in Logcat

## Project Structure

```
HydrogenBridgeApp/
├── app/
│   ├── build.gradle.kts          # App-level build config
│   ├── proguard-rules.pro        # ProGuard rules
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml
│           ├── java/com/yourcompany/hydrogenbridgeapp/
│           │   └── MainActivity.kt    # Main app logic
│           └── res/
│               ├── values/
│               │   └── strings.xml
│               └── xml/
│                   └── network_security_config.xml
├── build.gradle.kts               # Project-level build config
├── settings.gradle.kts
├── gradle.properties
└── web-integration-example.js     # JavaScript integration guide
```

## Next Steps

1. ✅ Update the web app URL in MainActivity.kt
2. ✅ Build the debug APK
3. ✅ Install on test device
4. ✅ Add JavaScript bridge code to your web app
5. ✅ Test payment flow
6. ✅ Build release APK for production

## Support

For issues with:
- **Hydrogen Neo integration**: Contact Hydrogen support
- **This bridge app**: Check the code comments or modify as needed
- **Android development**: Refer to Android documentation

## License

This is a template project. Modify and use as needed for your application.
# abiapay-pos-kotlin
