package com.yourcompany.hydrogenbridgeapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.webkit.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.yourcompany.hydrogenbridgeapp.commonsdk.printer.USBPrint
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private val PAYMENT_REQUEST_CODE = 100
    private var tvPrinterInfo: TextView? = null
    private var usbPrint: USBPrint? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable remote debugging for development
        WebView.setWebContentsDebuggingEnabled(true)

        // Initialize USBPrint with a listener
        usbPrint = USBPrint(this, USBPrint.OnTPRINTERSuccessListener { data ->
            runOnUiThread {
                tvPrinterInfo?.text = when (data) {
                    0 -> "Printer status: Normal"
                    16 -> "Printer status: No paper"
                    else -> "Printer status: Error ($data)"
                }
            }
        })

        webView = WebView(this)
        setupWebView()
        setContentView(webView)

        // Load your application URL
        webView.loadUrl("https://hydrogen.abiaone.com/signin/")
    }

    private fun setupWebView() {
        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            databaseEnabled = true
            setSupportZoom(true)
            useWideViewPort = true
            loadWithOverviewMode = true
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            
            // Fix for Android 10 WebView issues
            cacheMode = WebSettings.LOAD_DEFAULT
            allowFileAccess = true
            allowContentAccess = true
        }
        val cookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)
        cookieManager.setAcceptThirdPartyCookies(webView, true)

        // Register the bridges
        usbPrint?.let {
            webView.addJavascriptInterface(PrinterBridge(it), "HydrogenBridge")
        }
        webView.addJavascriptInterface(PaymentBridge(), "HydrogenPaymentBridge")

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                Log.d("WebView", "Page loaded: $url")
            }
        }
    }

    inner class PrinterBridge(private val printer: USBPrint) {
        @JavascriptInterface
        fun printReceipt(jsonString: String) {
            printer.printContent_58(jsonString, USBPrint.OnTPRINTERSuccessListener { data ->
                runOnUiThread {
                    tvPrinterInfo?.text = when (data) {
                        0 -> "Printer status: Normal"
                        16 -> "Printer status: No paper"
                        else -> "Printer status: Error ($data)"
                    }
                }
            })
        }
    }

    inner class PaymentBridge {
        @JavascriptInterface
        fun initiateCardPayment(amount: Float, customRef: String) {
            Log.d("HydrogenPayment", "initiateCardPayment: $amount")
            runOnUiThread {
                launchHydrogenIntent("com.hydrogen.card_payment", amount, customRef)
            }
        }

        @JavascriptInterface
        fun initiateTransfer(amount: Float, customRef: String) {
            Log.d("HydrogenPayment", "initiateTransfer: $amount")
            runOnUiThread {
                launchHydrogenIntent(
                    "com.hydrogen.transfer", amount,
                    customRef
                )
            }
        }

        private fun launchHydrogenIntent(action: String, amount: Float, customRef: String) {
            try {
                val intent = Intent(action).apply {
                    putExtra("REQUEST_KEY", amount)
                    putExtra("CUSTOM_REF_KEY", customRef)
                }
                startActivityForResult(intent, PAYMENT_REQUEST_CODE)
            } catch (e: Exception) {
                sendFullResultToWeb("FAILED", "Target App ($action) not found")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PAYMENT_REQUEST_CODE) {
            val resultString = data?.getStringExtra("RESULT_KEY") ?: ""
            val isApproved = resultString.contains("responseCode=00") || resultString.contains("SUCCESS")

            val status = when {
                resultCode == RESULT_OK && isApproved -> "SUCCESS"
                resultString.contains("CANCELLED") || resultCode == RESULT_CANCELED -> "CANCELLED"
                else -> "FAILED"
            }

            sendFullResultToWeb(status, resultString)
        }
    }

    private fun sendFullResultToWeb(status: String, rawResult: String) {
        val response = JSONObject().apply {
            put("status", status)
            put("raw", rawResult)
            
            val details = JSONObject()
            if (rawResult.contains("CardMessageData")) {
                try {
                    val content = rawResult.substringAfter("(").substringBeforeLast(")")
                    content.split(",").forEach { pair ->
                        val kv = pair.split("=")
                        if (kv.size == 2) {
                            details.put(kv[0].trim(), kv[1].trim())
                        }
                    }
                } catch (e: Exception) {
                    Log.e("HydrogenPayment", "Parse Error: ${e.message}")
                }
            } else {
                details.put("error_message", rawResult)
            }
            put("details", details)
        }

        webView.post {
            webView.evaluateJavascript(
                "if(window.handleHydrogenPaymentResult) { window.handleHydrogenPaymentResult($response); }",
                null
            )
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            @Suppress("DEPRECATION")
            super.onBackPressed()
        }
    }
    override fun onPause() {
        super.onPause()
        CookieManager.getInstance().flush()
    }

    override fun onDestroy() {
        usbPrint?.closePrinter()
        super.onDestroy()
    }
}