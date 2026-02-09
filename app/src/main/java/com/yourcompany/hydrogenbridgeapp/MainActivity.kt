package com.yourcompany.hydrogenbridgeapp

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageInfo
import android.os.Build
import android.os.Bundle
import android.webkit.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private val PAYMENT_REQUEST_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check WebView version
        checkWebViewVersion()

        // Enable WebView debugging
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true)
        }

        webView = WebView(this)
        setupWebView()

        setContentView(webView)
        webView.loadUrl("https://abiapay-pos-donice.vercel.app/signin")
    }

    private fun checkWebViewVersion() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val webViewPackage: PackageInfo? = WebView.getCurrentWebViewPackage()
                val version = webViewPackage?.versionName ?: "Unknown"
                android.util.Log.d("WebView", "WebView version: $version")

                // Show warning if version is too old
                if (version.startsWith("74") || version.startsWith("75")) {
                    Toast.makeText(
                        this,
                        "WebView is outdated (v$version). Please update Android System WebView from Play Store",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("WebView", "Error checking WebView version: ${e.message}")
        }
    }

    // Rest of your code stays the same...
    private fun setupWebView() {
        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            databaseEnabled = true
            setSupportZoom(true)
            builtInZoomControls = false
            loadWithOverviewMode = true
            useWideViewPort = true

            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            cacheMode = WebSettings.LOAD_DEFAULT
            allowFileAccess = true
            allowContentAccess = true

            userAgentString = "Mozilla/5.0 (Linux; Android 10) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Mobile Safari/537.36"

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)
            }
        }

        webView.addJavascriptInterface(PaymentBridge(), "HydrogenBridge")

        webView.webViewClient = object : WebViewClient() {
            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                super.onReceivedError(view, request, error)
                val errorMsg = "Error: ${error?.description}"
                android.util.Log.e("WebView", errorMsg)

                // Auto-reload on chunk errors
                if (error?.description?.contains("ChunkLoadError") == true ||
                    error?.description?.contains("Loading chunk") == true) {
                    Toast.makeText(this@MainActivity, "Reloading due to loading error...", Toast.LENGTH_SHORT).show()
                    view?.reload()
                }
            }
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
                android.util.Log.d("WebViewConsole", "${consoleMessage.message()} -- Line ${consoleMessage.lineNumber()}")

                // Auto-reload on chunk errors in console
                if (consoleMessage.message().contains("ChunkLoadError") ||
                    consoleMessage.message().contains("Loading chunk")) {
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "Reloading page...", Toast.LENGTH_SHORT).show()
                        webView.reload()
                    }
                }
                return true
            }
        }
    }

    inner class PaymentBridge {

        @JavascriptInterface
        fun initiateCardPayment(amount: Int) {
            runOnUiThread {
                launchHydrogenPayment("com.hydrogen.card_payment", amount)
            }
        }

        @JavascriptInterface
        fun initiateBreezePay(amount: Int) {
            runOnUiThread {
                launchHydrogenPayment("com.hydrogen.breezepay", amount)
            }
        }

        @JavascriptInterface
        fun initiateTransfer(amount: Int) {
            runOnUiThread {
                launchHydrogenPayment("com.hydrogen.transfer", amount)
            }
        }

        @JavascriptInterface
        fun showTransactionHistory() {
            runOnUiThread {
                launchHydrogenPayment("com.hydrogen.transaction_history", 0)
            }
        }

        @JavascriptInterface
        fun isHydrogenAppInstalled(): Boolean {
            val intent = Intent("com.hydrogen.card_payment")
            return intent.resolveActivity(packageManager) != null
        }
    }

    private fun launchHydrogenPayment(action: String, amount: Int) {
        try {
            val intent = Intent(action)
            if (amount > 0) {
                intent.putExtra("REQUEST_KEY", amount)
            }

            if (intent.resolveActivity(packageManager) != null) {
                startActivityForResult(intent, PAYMENT_REQUEST_CODE)
            } else {
                Toast.makeText(this, "Hydrogen Neo App is not installed", Toast.LENGTH_LONG).show()
                sendErrorToWebApp("Hydrogen Neo App not installed")
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error launching payment: ${e.message}", Toast.LENGTH_LONG).show()
            sendErrorToWebApp(e.message ?: "Unknown error")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PAYMENT_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    val result = data?.getStringExtra("RESULT_KEY") ?: "SUCCESS"
                    sendResultToWebApp(result, "SUCCESS")
                }
                Activity.RESULT_CANCELED -> {
                    val result = data?.getStringExtra("RESULT_KEY") ?: "Transaction cancelled"
                    sendResultToWebApp(result, "CANCELLED")
                }
                else -> {
                    val result = data?.getStringExtra("RESULT_KEY") ?: "Transaction failed"
                    sendResultToWebApp(result, "FAILED")
                }
            }
        }
    }

    private fun sendResultToWebApp(result: String, status: String) {
        val json = JSONObject().apply {
            put("status", status)
            put("data", result)
        }

        val jsCode = "if(typeof handleHydrogenPaymentResult === 'function') { handleHydrogenPaymentResult($json); }"
        webView.evaluateJavascript(jsCode, null)
    }

    private fun sendErrorToWebApp(error: String) {
        val json = JSONObject().apply {
            put("status", "ERROR")
            put("message", error)
        }

        val jsCode = "if(typeof handleHydrogenPaymentResult === 'function') { handleHydrogenPaymentResult($json); }"
        webView.evaluateJavascript(jsCode, null)
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}