package com.yourcompany.hydrogenbridgeapp.commonsdk.printer;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;


import com.pos.demosdk.BaseActivity;
import com.pos.demosdk.R;

import java.io.FileOutputStream;
import java.io.InputStream;

public class SetTextFontActivity extends BaseActivity {

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ttf);



        context= SetTextFontActivity.this;

        copy(context,"roboto.ttf","roboto.ttf");

        findViewById(R.id.bt_ttf).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent("android.intent.action.set.printer.mode");
                // intent.putExtra("printer_font_path","path");   //非等宽字体库
                intent.putExtra("printer_monoFont_path","/sdcard/roboto.ttf");   //等宽字体库
                sendBroadcast(intent);


            }
        });


    }


    public  boolean copy(Context myContext, String ASSETS_NAME,
                         String saveName) {
        boolean result=false;
        String filename = "/sdcard/" + saveName;

        try {
            //去掉文件检测  直接复制
            InputStream is = myContext.getResources().getAssets()
                    .open(ASSETS_NAME);
            FileOutputStream fos = new FileOutputStream(filename);
            byte[] buffer = new byte[7168];
            int count = 0;
            while ((count = is.read(buffer)) > 0) {
                fos.write(buffer, 0, count);
            }
            fos.close();
            is.close();

            result=true;

        } catch (Exception e) {
            e.printStackTrace();
            result=false;
        }

        return result;
    }



}