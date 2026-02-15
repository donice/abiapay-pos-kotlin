package com.yourcompany.hydrogenbridgeapp.commonsdk.printer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;

import com.common.apiutil.CommonException;
import com.common.apiutil.printer.UsbThermalPrinter;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.pos.demosdk.R;

import java.util.Hashtable;

public class USBPrint {

    Context mContext;
    UsbThermalPrinter mUsbThermalPrinter;
    private OnTPRINTERSuccessListener monTPRINTERSuccessListener;

    public interface OnTPRINTERSuccessListener {
        void onTPRINTERSuccess(int data);
    }

    public USBPrint(Context context,OnTPRINTERSuccessListener onTPRINTERSuccessListener) {
        this.mContext = context;
        this.monTPRINTERSuccessListener=onTPRINTERSuccessListener;
        mUsbThermalPrinter = new UsbThermalPrinter(mContext);
        new Thread(() -> {
            try {
                mUsbThermalPrinter.start(0);
                String version = mUsbThermalPrinter.getVersion();
                int status = mUsbThermalPrinter.checkStatus();

                if (monTPRINTERSuccessListener != null)
                    monTPRINTERSuccessListener.onTPRINTERSuccess(status);

                Log.d("printer version---",version);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void printContent_58(OnTPRINTERSuccessListener onTPRINTERSuccessListener){
        this.monTPRINTERSuccessListener=onTPRINTERSuccessListener;
        new Thread(() -> {
            try{
                mUsbThermalPrinter.reset();
                mUsbThermalPrinter.setAlgin(UsbThermalPrinter.ALGIN_MIDDLE);
                mUsbThermalPrinter.setGray(7);
                mUsbThermalPrinter.setAlgorithm(1);
                mUsbThermalPrinter.setBold(true);
                mUsbThermalPrinter.setMonoSpace(true);


                Bitmap barcode = CreateCode("12345678", BarcodeFormat.CODE_128, 320, 176);
                if (barcode != null) mUsbThermalPrinter.printLogo(barcode, true);
                Bitmap qrcode = CreateCode("12345678", BarcodeFormat.QR_CODE, 160, 160);
                if (qrcode != null) mUsbThermalPrinter.printLogo(qrcode, true);

                Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.telpoe);
                if(bitmap != null) mUsbThermalPrinter.printLogo(bitmap,true);
                mUsbThermalPrinter.addString(mContext.getString(R.string.printContent));
                mUsbThermalPrinter.setTextSize(26);
                mUsbThermalPrinter.addString(mContext.getString(R.string.printContent1));
                mUsbThermalPrinter.setTextSize(24);
                mUsbThermalPrinter.addString(mContext.getString(R.string.printContent1));
                mUsbThermalPrinter.setTextSize(22);
                mUsbThermalPrinter.addString(mContext.getString(R.string.printContent1));
                mUsbThermalPrinter.setTextSize(20);
                mUsbThermalPrinter.addString(mContext.getString(R.string.printContent1));
                mUsbThermalPrinter.setTextSize(20);
                mUsbThermalPrinter.addString(mContext.getString(R.string.printContent2));
                mUsbThermalPrinter.enlargeFontSize(1,2);
                mUsbThermalPrinter.addString(mContext.getString(R.string.printContent2));
                mUsbThermalPrinter.enlargeFontSize(2,1);
                mUsbThermalPrinter.addString(mContext.getString(R.string.printContent2));
                mUsbThermalPrinter.enlargeFontSize(2,2);
                mUsbThermalPrinter.addString(mContext.getString(R.string.printContent2));
                mUsbThermalPrinter.printString();
                mUsbThermalPrinter.walkPaper(12);
                   /*  mUsbThermalPrinter.setTextSize(24);
                mUsbThermalPrinter.setAlgin(UsbThermalPrinter.ALGIN_LEFT);
                mUsbThermalPrinter.setBold(true);
                mUsbThermalPrinter.addString("Standard LP");
                mUsbThermalPrinter.addString("A 22 25 33 36 50 52");
                mUsbThermalPrinter.addString(setLeftandRight("Ticket Number","#24073102015381081",mUsbThermalPrinter));
                mUsbThermalPrinter.addString(setLeftandRight("Ticket Prize($)","20.00",mUsbThermalPrinter));
                mUsbThermalPrinter.addString(setLeftandRight("Bet Date","2024/08/06 14:02:05",mUsbThermalPrinter));
                mUsbThermalPrinter.addString(setLeftandRight("Draw Date","2024/08/06 21:00:00",mUsbThermalPrinter));
                mUsbThermalPrinter.printLogo(generateReceiptBitmap(384,30,"Message:","Transaction Successful"),true);
                mUsbThermalPrinter.printLogo(generateReceiptBitmap(384,30,"Message:","Transaction Successful"),true);
                mUsbThermalPrinter.printLogo(generateReceiptBitmap(384,30,"Message:","Transaction Successful"),true);
                mUsbThermalPrinter.printLogo(generateReceiptBitmap(384,30,"Message:","Transaction Successful"),true);
                mUsbThermalPrinter.printLogo(generateReceiptBitmap(384,30,"Message:","Transaction Successful"),true);
                mUsbThermalPrinter.printLogo(generateReceiptBitmap(384,30,"Message:","Transaction Successful"),true);

                mUsbThermalPrinter.printString();
                mUsbThermalPrinter.walkPaper(12);*/

             /*   mUsbThermalPrinter.setTextSize(18);

                mUsbThermalPrinter.addString("1234567890123456789012345678901234567890");
                mUsbThermalPrinter.addColumnsString(new String[]{"Name",":","M.ADRIANSYAHAZHARI"},
                        new int[]{11,3,18}, new int[]{0,1,0},18);
                mUsbThermalPrinter.addColumnsString(new String[]{"Waktu Print",":","M.ADRIANSYAHAZHARIT"},
                        new int[]{11,3,18}, new int[]{0,1,0},18);
                mUsbThermalPrinter.printString();
                mUsbThermalPrinter.walkPaper(12);

                if (monTPRINTERSuccessListener != null)
                    monTPRINTERSuccessListener.onTPRINTERSuccess(0);*/

                /*mUsbThermalPrinter.addColumnsString(new String[]{"TERMINAL ID:","7111C192"},
                                      new int[]{2,3}, new int[]{0,2},18);

                mUsbThermalPrinter.addColumnsString(new String[]{"Name",":","M.ADRIANSYAHAZHARI" +
                                "AZHARI"},
                        new int[]{4,1,5}, new int[]{0,1,0},22);*/


            }catch (Exception e){
                e.printStackTrace();
                String result =e.toString();

                Log.e("yw",result);
                if (result.contains("NoPaperException")) {
                    if (monTPRINTERSuccessListener != null)
                        monTPRINTERSuccessListener.onTPRINTERSuccess(-1);

                } else if (result.contains("OverHeatException")) {
                    if (monTPRINTERSuccessListener != null)
                        monTPRINTERSuccessListener.onTPRINTERSuccess(-2);

                } else {
                    if (monTPRINTERSuccessListener != null)
                        monTPRINTERSuccessListener.onTPRINTERSuccess(-3);

                }
            }
        }).start();
    }

    public void closePrinter(){
        try {
            if(mUsbThermalPrinter != null) mUsbThermalPrinter.stop();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public Bitmap CreateCode(String str, BarcodeFormat type, int bmpWidth, int bmpHeight)
            throws WriterException {
        Hashtable<EncodeHintType, String> mHashtable = new Hashtable<EncodeHintType, String>();
        mHashtable.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        BitMatrix matrix = new MultiFormatWriter().encode(str, type, bmpWidth, bmpHeight, mHashtable);
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (matrix.get(x, y)) {
                    pixels[y * width + x] = 0xff000000;
                } else {
                    pixels[y * width + x] = 0xffffffff;
                }
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    public String setLeftandRight(String left,String right,UsbThermalPrinter mUsbThermalPrinter){
        String line="";

        try {
            int i = mUsbThermalPrinter.measureText(left + right);

        int i1 = mUsbThermalPrinter.measureText(" ");
        int SpaceNumber=(384-i)/i1;
        String spaceString = "";
        for (int j=0;j<SpaceNumber;j++){
            spaceString+=" ";
        }

        line=left+spaceString+right;

        } catch (CommonException e) {
            throw new RuntimeException(e);
        }
        return line;
    }

    public Bitmap generateReceiptBitmap(int width,int height,String leftString,String rightString){
        Bitmap bitmap  = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE); // Set the background color to white

        //ResourcesCompat.getFont(context, R.font.poppins_regular);//设置字体
        // Create a paint object for drawing text
        Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);
        paint.setTextSize(24f);
        // paint.setTypeface();
        paint.setFakeBoldText(true);

        // Calculate the y position for the text
        float yPos = 30f;
        // Draw left-aligned text
        canvas.drawText(leftString, 0f, yPos, paint);
        // Measure the width of the right-aligned text
        float textWidth = paint.measureText(rightString);
        // Draw right-aligned text
        canvas.drawText(rightString, width - textWidth, yPos, paint);


        //90度

        return bitmap;

        //return rotateBitmap90Degrees(bitmap);

    }

    public static Bitmap rotateBitmap90Degrees(Bitmap source) {
        int width = source.getHeight(); // 旋转90度后，宽度变为原高度
        int height = source.getWidth(); // 旋转90度后，高度变为原宽度

        Matrix matrix = new Matrix();
        matrix.postRotate(90);

        return Bitmap.createBitmap(
                source,
                0,
                0,
                source.getWidth(),
                source.getHeight(),
                matrix,
                true
        );
    }

    public static Bitmap rotateBitmap90Degrees111(Bitmap source) {
        if (source == null) {
            return null;
        }

        int width = source.getWidth();
        int height = source.getHeight();

        // 创建一个新的Bitmap，其宽度和高度是原始Bitmap的高度和宽度（因为旋转90度会交换这两个值）
        Bitmap rotatedBitmap = Bitmap.createBitmap(height, width, source.getConfig());

        // 创建一个Matrix对象并设置旋转90度
        Matrix matrix = new Matrix();
        matrix.postRotate(90);

        // 使用Matrix将原始Bitmap绘制到新的Bitmap上
        Paint paint = new Paint();
        // 正确的做法是使用Canvas
        Canvas canvas = new Canvas(rotatedBitmap);
        canvas.concat(matrix);
        canvas.drawBitmap(source, -width / 2, -height / 2, paint); // 注意调整偏移量以确保图像居中
        return rotatedBitmap;
    }


    }
