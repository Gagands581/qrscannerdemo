package com.reader.qrcode;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.reader.qrcode.constants.AppConstants;

public class ScannerResultActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();

    private TextView txtview_results;
    private Button btn_scanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner_result);

        initUI();
    }

    private void initUI() {
        txtview_results = (TextView) findViewById(R.id.txtview_results);

        btn_scanner = (Button) findViewById(R.id.btn_scanner);
        btn_scanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), QRCodeScannerActivity.class);
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                txtview_results.setVisibility(View.VISIBLE);
                txtview_results.setText(data.getStringExtra(AppConstants.Intent_QRCode_Data));
            } else if (resultCode == RESULT_CANCELED) {
                // do nothing
            }
        }
    }
}
