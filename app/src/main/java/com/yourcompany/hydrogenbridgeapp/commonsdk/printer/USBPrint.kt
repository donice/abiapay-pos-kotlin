package com.yourcompany.hydrogenbridgeapp.commonsdk.printer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.common.apiutil.printer.UsbThermalPrinter
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.yourcompany.hydrogenbridgeapp.R
import org.json.JSONObject
import java.util.Hashtable

class USBPrint(private val mContext: Context, private var monTPRINTERSuccessListener: OnTPRINTERSuccessListener?) {

    private val mUsbThermalPrinter: UsbThermalPrinter = UsbThermalPrinter(mContext)

    fun interface OnTPRINTERSuccessListener {
        fun onTPRINTERSuccess(data: Int)
    }

    init {
        Thread {
            try {
                mUsbThermalPrinter.start(0)
                val version = mUsbThermalPrinter.version
                val status = mUsbThermalPrinter.checkStatus()
                monTPRINTERSuccessListener?.onTPRINTERSuccess(status)
                Log.d("printer version---", version)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

    fun printContent_58(jsonData: String, successListener: OnTPRINTERSuccessListener? = null) {
        if (successListener != null) {
            this.monTPRINTERSuccessListener = successListener
        }
        Thread {
            try {
                // 1. Parse JSON - Ensuring we match the Next.js keys exactly
                val data = JSONObject(jsonData)
                val abssin = data.optString("abssin", "N/A")
                val revType = data.optString("rev_type", "N/A")
                val tpName = data.optString("tp_name", "N/A")
                val transRef = data.optString("transref", "N/A")
                val qrRef = data.optString("qr_ref", "N/A")
                val date = data.optString("transdate", "N/A")
                val totalAmount = data.optString("amount", "0.00")
                val phoneNumber = data.optString("phone_number", "N/A")
                val fiscalYear = data.optString("fiscal_year", "N/A")
                val mdaName = data.optString("mda_name", "ABIAPAY")
                val items = data.optJSONArray("items")
                val isDuplicate = data.optBoolean("is_duplicate", false)

                // 2. Printer Initialization
                mUsbThermalPrinter.reset()
                mUsbThermalPrinter.setGray(7)
                mUsbThermalPrinter.setAlgorithm(1)

                // 3. Header & Logo
                mUsbThermalPrinter.setAlgin(UsbThermalPrinter.ALGIN_MIDDLE)

                if (isDuplicate) {
                    mUsbThermalPrinter.setBold(true)
                    mUsbThermalPrinter.setTextSize(24)
                    mUsbThermalPrinter.addString("**** DUPLICATE RECEIPT ****")
                    mUsbThermalPrinter.addString("Original already issued")
                    mUsbThermalPrinter.addString("--------------------------------")
                    mUsbThermalPrinter.setBold(false)
                }
                val logo = BitmapFactory.decodeResource(mContext.resources, R.mipmap.abia)
                logo?.let { mUsbThermalPrinter.printLogo(it, true) }

                mUsbThermalPrinter.setBold(true)
                mUsbThermalPrinter.setTextSize(24)
                mUsbThermalPrinter.addString("ABIA STATE GOVERNMENT")
                mUsbThermalPrinter.addString("ABIA STATE INTERNAL REVENUE SERVICE")
                mUsbThermalPrinter.addString("Revenue Receipt")



                mUsbThermalPrinter.setBold(false)
                mUsbThermalPrinter.setTextSize(20)
                mUsbThermalPrinter.addString("Official Revenue Receipt")
                mUsbThermalPrinter.addString("--------------------------------")

                // 4. Payer Information
                mUsbThermalPrinter.setAlgin(UsbThermalPrinter.ALGIN_LEFT)
                mUsbThermalPrinter.setTextSize(22)

                // Using your setLeftandRight for clean columns
                mUsbThermalPrinter.addString(setLeftandRight("PAYER ABSSIN:", abssin))
                mUsbThermalPrinter.addString(setLeftandRight("PAYER NAME:", tpName))
                mUsbThermalPrinter.addString(setLeftandRight("PHONE:", phoneNumber))
                mUsbThermalPrinter.addString(setLeftandRight("REF:", transRef))
                mUsbThermalPrinter.addString(setLeftandRight("PAYMENT DATE:", date))
                mUsbThermalPrinter.addString(setLeftandRight("YEAR:", fiscalYear))
                mUsbThermalPrinter.addString("------------------------------")

                mUsbThermalPrinter.setTextSize(24)
                mUsbThermalPrinter.addString(setLeftandRight("MDA",mdaName.uppercase()))
                mUsbThermalPrinter.addString(setLeftandRight("Revenue Type:",revType))
                // 5. Dynamic Items Table
                mUsbThermalPrinter.setBold(true)
                mUsbThermalPrinter.addString(setLeftandRight("REVENUE ITEM", "AMOUNT"))
                mUsbThermalPrinter.setBold(false)

                items?.let {
                    for (i in 0 until it.length()) {
                        val item = it.getJSONObject(i)
                        // Use the key 'revenue_item' as defined in your Next.js mapping
                        val name = item.optString("revenue_item", "Service/Tax")
                        val amt = item.optString("amount", "0.00")

                        // If item name is too long, it might wrap.
                        // setLeftandRight handles the spacing calculation.
                        mUsbThermalPrinter.addString(setLeftandRight(name, "N$amt"))
                    }
                }

                // 6. Total and Status
                mUsbThermalPrinter.addString("--------------------------------")
                mUsbThermalPrinter.setBold(true)
                mUsbThermalPrinter.setTextSize(28)
                mUsbThermalPrinter.addString(setLeftandRight("TOTAL PAID", "NGN $totalAmount"))

                // --- STATUS WATERMARK STYLE ---
                mUsbThermalPrinter.setAlgin(UsbThermalPrinter.ALGIN_MIDDLE)
                mUsbThermalPrinter.walkPaper(2)
                mUsbThermalPrinter.setTextSize(35) // Large font for visibility
                mUsbThermalPrinter.addString("*** PAID ***")
                mUsbThermalPrinter.walkPaper(2)
                // ------------------------------

                // 7. Verification & Footer
                mUsbThermalPrinter.setBold(false)
                mUsbThermalPrinter.setTextSize(20)

                // Generate QR Code for verification
                val qrCode = createCode(qrRef, BarcodeFormat.QR_CODE, 180, 180)
                qrCode?.let { mUsbThermalPrinter.printLogo(it, true) }

                mUsbThermalPrinter.setTextSize(18)
                mUsbThermalPrinter.addString("Scan to verify transaction")

                // Custom Footer Message
                mUsbThermalPrinter.walkPaper(2)
                mUsbThermalPrinter.addString("Thank you for performing your civic duty.")
                mUsbThermalPrinter.addString("For inquiries, visit abiapay.com")
                mUsbThermalPrinter.setBold(true)
                mUsbThermalPrinter.addString("Powered by AbiaPay")

                // 8. Execute Print
                mUsbThermalPrinter.printString()
                mUsbThermalPrinter.walkPaper(25) // Enough space to tear without cutting the text

                monTPRINTERSuccessListener?.onTPRINTERSuccess(0)

            } catch (e: Exception) {
                Log.e("USBPrint", "Print Error: ${e.message}")
                handleError(e)
            }
        }.start()
    }
    private fun handleError(e: Exception) {
        val errorStr = e.toString()
        val code = when {
            errorStr.contains("NoPaperException") -> -1
            errorStr.contains("OverHeatException") -> -2
            else -> -3
        }
        monTPRINTERSuccessListener?.onTPRINTERSuccess(code)
    }

    fun closePrinter() {
        try {
            mUsbThermalPrinter.stop()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun createCode(str: String, type: BarcodeFormat, bmpWidth: Int, bmpHeight: Int): Bitmap? {
        val hints = Hashtable<EncodeHintType, String>()
        hints[EncodeHintType.CHARACTER_SET] = "UTF-8"
        val matrix = MultiFormatWriter().encode(str, type, bmpWidth, bmpHeight, hints)
        val width = matrix.width
        val height = matrix.height
        val pixels = IntArray(width * height)
        for (y in 0 until height) {
            for (x in 0 until width) {
                pixels[y * width + x] = if (matrix.get(x, y)) -0x1000000 else -0x1
            }
        }
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return bitmap
    }

    private fun setLeftandRight(left: String, right: String): String {
        return try {
            val totalWidth = 384
            val leftWidth = mUsbThermalPrinter.measureText(left)
            val rightWidth = mUsbThermalPrinter.measureText(right)
            val spaceWidth = mUsbThermalPrinter.measureText(" ")

            val spaceCount = (totalWidth - (leftWidth + rightWidth)) / spaceWidth
            val spaces = " ".repeat(if (spaceCount > 0) spaceCount else 1)

            left + spaces + right
        } catch (e: Exception) {
            "$left $right"
        }
    }
}