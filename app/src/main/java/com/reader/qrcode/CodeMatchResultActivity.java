package com.reader.qrcode;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.reader.qrcode.constants.AppConstants;

public class CodeMatchResultActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();

    private EditText edittext_secretcode;
    private TextView txtview_err_message;
    private Button btn_navigate_back, btn_navigate_forward;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_match_result);

        initUI();
    }

    /**
     * initUI
     * - Initializes the UI Components of the screen
     */
    private void initUI() {

        edittext_secretcode = (EditText) findViewById(R.id.edittext_secretcode);
        txtview_err_message = (TextView) findViewById(R.id.txtview_err_message);

        btn_navigate_back = (Button) findViewById(R.id.btn_navigate_back);
        btn_navigate_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED, getIntent());
                finish();
            }
        });

        btn_navigate_forward = (Button) findViewById(R.id.btn_navigate_forward);
        btn_navigate_forward.setTag(Actions.VerifySecret.name());
        btn_navigate_forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btn_navigate_forward.getTag().equals(Actions.VerifySecret.name())) {
                    String code = edittext_secretcode.getText().toString();
                    if (code.isEmpty()) {
                        Toast.makeText(view.getContext(), getString(R.string.err_empty_secret_code), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String secretcode = getIntent().getStringExtra(AppConstants.Intent_SecretCode);
                    txtview_err_message.setVisibility(View.VISIBLE);

                    Log.d(TAG, "Prev Code : " + secretcode + ", Entered Code : " + code);

                    if (code.equals(secretcode)) {
                        txtview_err_message.setText(getString(R.string.msg_secretcode_matched));
                        btn_navigate_forward.setText(getString(R.string.btn_secret_proceed));
                        btn_navigate_forward.setTag(Actions.CallScanner.name());
                    } else {
                        txtview_err_message.setText(getString(R.string.err_secretcode_not_matching));
                        btn_navigate_back.setVisibility(View.VISIBLE);
                    }
                } else if (btn_navigate_forward.getTag().equals(Actions.CallScanner.name())) {
                    // Moves back to the previous screen to properly finsih it and then proceed to QR Code initializer scanner screen
                    setResult(RESULT_OK, getIntent());
                    finish();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED, getIntent());
        finish();

    }

    /**
     * Based on the matching secret code, button is set to the suitable action
     */
    private enum Actions {
        VerifySecret, CallScanner
    }
}
