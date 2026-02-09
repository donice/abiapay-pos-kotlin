/**
 * JavaScript Integration Guide for Hydrogen Bridge App
 * 
 * Add this code to your web app to communicate with the native Android app
 */

// Check if running inside the Android bridge app
function isAndroidBridge() {
    return typeof HydrogenBridge !== 'undefined';
}

// Check if Hydrogen Neo App is installed
function checkHydrogenAppInstalled() {
    if (isAndroidBridge()) {
        return HydrogenBridge.isHydrogenAppInstalled();
    }
    return false;
}

// Initiate Card Payment
function initiateCardPayment(amount) {
    if (isAndroidBridge()) {
        // Amount should be in smallest currency unit (e.g., 5000 = 50.00)
        HydrogenBridge.initiateCardPayment(amount);
    } else {
        console.error('Not running in Android bridge app');
        alert('This feature only works in the mobile app');
    }
}

// Initiate BreezePay
function initiateBreezePay(amount) {
    if (isAndroidBridge()) {
        HydrogenBridge.initiateBreezePay(amount);
    } else {
        console.error('Not running in Android bridge app');
    }
}

// Initiate Transfer (InstantPay)
function initiateTransfer(amount) {
    if (isAndroidBridge()) {
        HydrogenBridge.initiateTransfer(amount);
    } else {
        console.error('Not running in Android bridge app');
    }
}

// Show Transaction History
function showTransactionHistory() {
    if (isAndroidBridge()) {
        HydrogenBridge.showTransactionHistory();
    } else {
        console.error('Not running in Android bridge app');
    }
}

// Handle payment result from native app
// This function MUST be defined in your web app
function handleHydrogenPaymentResult(result) {
    console.log('Payment Result:', result);
    
    if (result.status === 'SUCCESS') {
        console.log('Payment successful!', result.data);
        // Update your UI for success
        alert('Payment Successful!\n' + result.data);
        
    } else if (result.status === 'CANCELLED') {
        console.log('Payment cancelled', result.data);
        // Handle cancellation
        alert('Payment Cancelled');
        
    } else if (result.status === 'FAILED') {
        console.log('Payment failed', result.data);
        // Handle failure
        alert('Payment Failed: ' + result.data);
        
    } else if (result.status === 'ERROR') {
        console.error('Error:', result.message);
        alert('Error: ' + result.message);
    }
}

// Example usage in your HTML
/*
<!DOCTYPE html>
<html>
<head>
    <title>Payment Test</title>
    <script src="hydrogen-bridge.js"></script>
</head>
<body>
    <h1>Hydrogen Payment Bridge</h1>
    
    <div id="status"></div>
    
    <button onclick="processPayment()">Pay ₦50.00</button>
    <button onclick="showTransactionHistory()">View History</button>
    
    <script>
        // Check if bridge is available
        if (isAndroidBridge()) {
            document.getElementById('status').innerHTML = 
                'Running in Android App ✓<br>' +
                'Hydrogen App: ' + (checkHydrogenAppInstalled() ? 'Installed ✓' : 'Not Installed ✗');
        } else {
            document.getElementById('status').innerHTML = 'Running in Browser (Bridge not available)';
        }
        
        function processPayment() {
            // Amount in kobo/cents: 5000 = ₦50.00
            initiateCardPayment(5000);
        }
    </script>
</body>
</html>
*/
