package com.yourcompany.hydrogenbridgeapp.commonsdk;


import static androidx.core.content.ContextCompat.startActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.pos.demosdk.BaseActivity;
import com.yourcompany.hydrogenbridgeapp.R;
import com.yourcompany.hydrogenbridgeapp.commonsdk.CommonSDKActivity;
import com.yourcompany.hydrogenbridgeapp.commonsdk.printer.PrintActivity;


public class CommonSDKActivity extends BaseActivity {

    TextView tv_name,tv_version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commonsdk_main);

        tv_name=findViewById(R.id.tv_name);
        tv_name.setText("COMMON SDK");

        findViewById(R.id.printer_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CommonSDKActivity.this, PrintActivity.class));
            }
        })

    }
}