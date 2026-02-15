package com.yourcompany.hydrogenbridgeapp

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.webkit.*
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.yourcompany.hydrogenbridgeapp.commonsdk.printer.USBPrint
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private val PAYMENT_REQUEST_CODE = 100
    private val PRINT_REQUEST_CODE = 101
    private var tvPrinterInfo: TextView? = null
    private var usbPrint: USBPrint? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable remote debugging for development
        WebView.setWebContentsDebuggingEnabled(true)

        usbPrint = USBPrint(this) { data ->
            runOnUiThread {
                tvPrinterInfo?.text = when (data) {
                    0 -> "Printer status:Normal"
                    16 -> "Printer status:No paper"
                    else -> "Printer status:Error"
                }
            }
        }
        webView = WebView(this)
        setupWebView()
        setContentView(webView)

        // Load your application URL
        webView.loadUrl("https://abiapay-pos.vercel.app/signin/")
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

        // Register the bridge
        webView.addJavascriptInterface(PaymentBridge(), "HydrogenBridge")

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                Log.d("WebView", "Page loaded: $url")
            }
        }
    }

    inner class PrinterBridge(private val usbPrint: USBPrint) {

        @JavascriptInterface
        fun sendToPrinter(jsonString: String) {
            usbPrint.printContent(jsonString) { status ->
                // You can log status or send it back to JS
                Log.d("Bridge", "Printer Status: $status")
            }
        }
    }
    inner class PaymentBridge {

        @JavascriptInterface
        fun initiateCardPayment(amount: Float) {
            Log.d("HydrogenPayment", "initiateCardPayment: $amount")
            runOnUiThread {
                launchHydrogenIntent("com.hydrogen.card_payment", amount)
            }
        }

        @JavascriptInterface
        fun initiateTransfer(amount: Float) {
            Log.d("HydrogenPayment", "initiateTransfer: $amount")
            runOnUiThread {
                launchHydrogenIntent("com.hydrogen.transfer", amount)
            }
        }

        private fun launchHydrogenIntent(action: String, amount: Float) {
            try {
                val intent = Intent(action).apply {
                    putExtra("REQUEST_KEY", amount)
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

        when (requestCode) {
            PAYMENT_REQUEST_CODE -> {
                val resultString = data?.getStringExtra("RESULT_KEY") ?: ""
                val isApproved = resultString.contains("responseCode=00") || resultString.contains("SUCCESS")

                val status = when {
                    resultCode == Activity.RESULT_OK && isApproved -> "SUCCESS"
                    resultString.contains("CANCELLED") || resultCode == Activity.RESULT_CANCELED -> "CANCELLED"
                    else -> "FAILED"
                }

                sendFullResultToWeb(status, resultString)
            }
            PRINT_REQUEST_CODE -> {
                val resultString = data?.getStringExtra("RESULT_KEY") ?: ""
                Log.d("HydrogenPayment", "Print result: $resultString")
            }
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
        finish()
        @Suppress("DEPRECATION")
        super.onBackPressed()
    }

    override fun onDestroy() {
        usbPrint?.closePrinter()
        super.onDestroy()
    }
}
