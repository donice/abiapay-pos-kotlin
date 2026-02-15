package com.yourcompany.hydrogenbridgeapp.commonsdk.printer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.util.Log
import com.common.apiutil.CommonException
import com.common.apiutil.printer.UsbThermalPrinter
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import org.json.JSONObject
import com.google.zxing.common.BitMatrix
import com.yourcompany.hydrogenbridgeapp.R
import java.util.Hashtable
import androidx.core.graphics.createBitmap

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

    fun printContent(jsonData: String, listener: OnTPRINTERSuccessListener?) {
        this.monTPRINTERSuccessListener = listener

        Thread {
            try {
                val data = JSONObject(jsonData)

                mUsbThermalPrinter.reset()
                mUsbThermalPrinter.setAlgin(UsbThermalPrinter.ALGIN_MIDDLE)
                mUsbThermalPrinter.setGray(data.optInt("gray", 7))
                mUsbThermalPrinter.setBold(data.optBoolean("bold", true))

                // 1. Dynamic Barcode
                val barcode = data.optString("barcode", "")
                if (barcode.isNotEmpty()) {
                    createCode(barcode, BarcodeFormat.CODE_128, 320, 176)?.let {
                        mUsbThermalPrinter.printLogo(it, true)
                    }
                }

                // 2. Dynamic QR Code
                val qrCode = data.optString("qrCode", "")
                if (qrCode.isNotEmpty()) {
                    createCode(qrCode, BarcodeFormat.QR_CODE, 200, 200)?.let {
                        mUsbThermalPrinter.printLogo(it, true)
                    }
                }

                // 3. Dynamic Text Content
                val title = data.optString("title", "")
                if (title.isNotEmpty()) {
                    mUsbThermalPrinter.setTextSize(30)
                    mUsbThermalPrinter.addString(title)
                }

                val body = data.optString("body", "")
                if (body.isNotEmpty()) {
                    mUsbThermalPrinter.setTextSize(24)
                    mUsbThermalPrinter.addString(body)
                }

                // 4. Execution
                mUsbThermalPrinter.printString()
                mUsbThermalPrinter.walkPaper(data.optInt("walkPaper", 12))

                monTPRINTERSuccessListener?.onTPRINTERSuccess(0)
            } catch (e: Exception) {
                Log.e("USBPrint", "Print Error: ${e.message}")
                monTPRINTERSuccessListener?.onTPRINTERSuccess(-3)
            }
        }.start()
    }
    fun closePrinter() {
        try {
            mUsbThermalPrinter.stop()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Throws(WriterException::class)
    fun createCode(str: String, type: BarcodeFormat, bmpWidth: Int, bmpHeight: Int): Bitmap? {
        return try {
            val hints = Hashtable<EncodeHintType, String>()
            hints[EncodeHintType.CHARACTER_SET] = "UTF-8"
            val matrix = MultiFormatWriter().encode(str, type, bmpWidth, bmpHeight, hints)

            val width = matrix.width
            val height = matrix.height
            val pixels = IntArray(width * height)
            for (y in 0 until height) {
                for (x in 0 until width) {
                    pixels[y * width + x] = if (matrix.get(x, y)) Color.BLACK else Color.WHITE
                }
            }
            createBitmap(width, height, Bitmap.Config.RGB_565).apply {
                setPixels(pixels, 0, width, 0, 0, width, height)
            }
        } catch (e: WriterException) {
            Log.e("ZXing", "Encoding failed", e)
            null // Return null so the printer just skips this image
        }
    }

    fun setLeftandRight(left: String, right: String, printer: UsbThermalPrinter): String {
        return try {
            val totalWidth = 384
            val textWidth = printer.measureText(left + right)
            val spaceWidth = printer.measureText(" ")
            val spaceCount = (totalWidth - textWidth) / spaceWidth
            val spaces = " ".repeat(kotlin.math.max(0, spaceCount))
            "$left$spaces$right"
        } catch (e: CommonException) {
            throw RuntimeException(e)
        }
    }

    private fun formatLine(left: String, right: String, totalWidth: Int): String {
        val spaceCount = totalWidth - left.length - right.length
        return if (spaceCount > 0) {
            left + " ".repeat(spaceCount) + right
        } else {
            // If text is too long, just put one space
            "$left $right"
        }
    }
    fun generateReceiptBitmap(width: Int, height: Int, leftString: String, rightString: String): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)

        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            textSize = 24f
            isFakeBoldText = true
        }

        val yPos = 30f
        canvas.drawText(leftString, 0f, yPos, paint)
        val textWidth = paint.measureText(rightString)
        canvas.drawText(rightString, width - textWidth, yPos, paint)

        return bitmap
    }

    companion object {
        fun rotateBitmap90Degrees(source: Bitmap): Bitmap {
            val matrix = Matrix().apply { postRotate(90f) }
            return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
        }
    }
}
