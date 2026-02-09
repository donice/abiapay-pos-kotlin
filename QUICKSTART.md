# Quick Start Guide

## 🚀 Get Started in 3 Steps

### 1️⃣ Configure Your Web App URL

Open `app/src/main/java/com/yourcompany/hydrogenbridgeapp/MainActivity.kt`

Find line ~27 and replace with your web app URL:
```kotlin
webView.loadUrl("https://your-web-app-url.com")
```

### 2️⃣ Build the App

**Option A: Using VS Code**
- Press `Ctrl+Shift+P` (Cmd+Shift+P on Mac)
- Type "Tasks: Run Task"
- Select "Build Debug APK"

**Option B: Using Terminal**
```bash
# Linux/Mac
./gradlew assembleDebug

# Windows
gradlew.bat assembleDebug
```

### 3️⃣ Install on Your Device

**Find your APK:**
```
app/build/outputs/apk/debug/app-debug.apk
```

**Install it:**
- Transfer to your Android device
- Open and install (enable "Install from Unknown Sources" if needed)

## 📱 Add Payment to Your Web App

Add this JavaScript to your web app:

```javascript
// Check if running in the Android app
function isAndroidBridge() {
    return typeof HydrogenBridge !== 'undefined';
}

// Trigger payment
function makePayment() {
    if (isAndroidBridge()) {
        // Amount in smallest unit (5000 = ₦50.00)
        HydrogenBridge.initiateCardPayment(5000);
    } else {
        alert('This feature only works in the mobile app');
    }
}

// Handle the result (REQUIRED!)
function handleHydrogenPaymentResult(result) {
    if (result.status === 'SUCCESS') {
        alert('Payment Successful! ' + result.data);
    } else if (result.status === 'CANCELLED') {
        alert('Payment Cancelled');
    } else if (result.status === 'FAILED') {
        alert('Payment Failed: ' + result.data);
    }
}
```

## ✅ Testing Checklist

- [ ] Install Hydrogen Neo App on test device first
- [ ] Install your bridge app
- [ ] Open bridge app (should load your web app)
- [ ] Click payment button in your web app
- [ ] Hydrogen app should open
- [ ] Complete payment
- [ ] Result should return to your web app

## 🆘 Common Issues

**"Gradle build failed"**
```bash
./gradlew clean
./gradlew build --refresh-dependencies
```

**"Web app not loading"**
- Check your internet connection
- Verify the URL is correct
- Make sure it's https:// (or update network_security_config.xml for http)

**"Hydrogen app not found"**
- Install Hydrogen Neo App from official source
- Verify it's installed on the same device

## 📚 More Help

See `README.md` for detailed documentation.

## 🎯 What You Have

✅ Complete Android app with WebView  
✅ JavaScript bridge for Hydrogen payments  
✅ Support for all payment types  
✅ VS Code integration  
✅ Ready to customize

Just update the URL and build! 🚀
