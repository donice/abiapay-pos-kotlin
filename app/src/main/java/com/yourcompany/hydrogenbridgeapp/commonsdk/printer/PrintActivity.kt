package com.yourcompany.hydrogenbridgeapp.commonsdk.printer

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.yourcompany.hydrogenbridgeapp.MainActivity
import com.yourcompany.hydrogenbridgeapp.R

class PrintActivity : MainActivity() {

    private var btnPrint: Button? = null
    private var usbPrint: USBPrint? = null
    private var tvName: TextView? = null
    private var tvPrinterInfo: TextView? = null
    private var tvVersion: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_print)
        init()
    }

    private fun init() {
        tvName?.text = "Printer Sample"

        tvVersion = findViewById(R.id.tv_version)
        tvVersion?.visibility = View.GONE

        
        usbPrint = USBPrint(this) { data ->
            runOnUiThread {
                tvPrinterInfo?.text = when (data) {
                    0 -> "Printer status:Normal"
                    16 -> "Printer status:No paper"
                    else -> "Printer status:Error"
                }
            }
        }
        
        btnPrint = findViewById(R.id.printtest_btn)
        btnPrint?.setOnClickListener {
            print()
        }
    }

    private fun print() {
        usbPrint?.printContent_58 { data ->
            runOnUiThread {
                tvPrinterInfo?.text = when (data) {
                    0 -> "Print success"
                    -1 -> {
                        Toast.makeText(this, "NoPaperException", Toast.LENGTH_LONG).show()
                        "Printer status:NoPaperException"
                    }
                    -2 -> {
                        Toast.makeText(this, "OverHeatException", Toast.LENGTH_LONG).show()
                        "Printer status:OverHeatException"
                    }
                    -3 -> {
                        Toast.makeText(this, "Print error", Toast.LENGTH_LONG).show()
                        "Printer status:Error"
                    }
                    else -> "Printer status:Normal"
                }
            }
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
