package com.reader.qrcode;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.reader.qrcode.constants.AppConstants;
import com.reader.qrcode.utils.PatternsUtils;

public class MainActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();
    private Context context;
    private EditText editext_phonenumber;
    private TextView txtview_secretnumber;
    private Button btn_action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();

    }

    private void initUI() {
        editext_phonenumber = (EditText) findViewById(R.id.editext_phonenumber);
        txtview_secretnumber = (TextView) findViewById(R.id.txtview_secretnumber);

        btn_action = (Button) findViewById(R.id.btn_action);
        btn_action.setTag(ScreenMode.PhoneVerify.name());

        btn_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (btn_action.getTag().equals(ScreenMode.PhoneVerify.name())) {
                    String phone = editext_phonenumber.getText().toString();
                    String message = validatePhone(phone);

                    if (message != null) {
                        Toast.makeText(view.getContext(), message, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // change the action of button
                    btn_action.setTag(ScreenMode.SecretCode.name());
                    btn_action.setText(getString(R.string.btn_action_phone_next));

                    // display the random 5 digit secret code
                    txtview_secretnumber.setVisibility(View.VISIBLE);
                    regenerateRandomnumber();

                } else if (btn_action.getTag().equals(ScreenMode.SecretCode.name())) {
                    // Navigate to the code matching screen ..
                    Intent intent = new Intent(view.getContext(), CodeMatchResultActivity.class);
                    intent.putExtra(AppConstants.Intent_SecretCode, txtview_secretnumber.getText().toString());
                    startActivityForResult(intent, 1);
                }
            }

            private String validatePhone(String phone) {
                String message = null;

                if (phone.isEmpty())
                    message = getString(R.string.err_phone_empty);
                else if (!PatternsUtils.isMatching(PatternsUtils.PatternExp.Phone, phone))
                    message = getString(R.string.err_phone_invalid);

                return message;
            }
        });

    }

    private void regenerateRandomnumber() {
        int number = (int) Math.round(Math.random() * 100000);
        txtview_secretnumber.setText(String.valueOf(number));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_CANCELED) {
                regenerateRandomnumber();
            } else if (resultCode == RESULT_OK) {
                Intent intent = new Intent(getApplicationContext(), ScannerResultActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    private enum ScreenMode {
        PhoneVerify, SecretCode
    }
}
