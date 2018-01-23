package com.reader.qrcode;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
    private Activity activity;
    private EditText editext_phonenumber;
    private TextView txtview_secretnumber;
    private Button btn_action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;
        activity = this;
        checkForPermissionModel();
    }

    private void checkForPermissionModel() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestCameraPermission();
            } else
                initUI();
        } else
            initUI();

    }

    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Camera Permission Required");
            builder.setMessage("This application requires the camera to perform QRCode Scanning");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, 1);
                }
            });

            builder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
        } else
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, 1);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length == 0) {
                // TODO: ask again for permissions

            } else {
                boolean isPermitted = (grantResults[0] == PackageManager.PERMISSION_GRANTED);
                if (isPermitted) {
                    initUI();
                } else {
                    Toast.makeText(context, "Camera permission is not provided", Toast.LENGTH_SHORT).show();
                }


            }
        }
    }

    /**
     * initUI
     * - Initializes the UI components for the screen
     */
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

            /**
             * Validates the phone number
             * @param phone
             * @return message
             */
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

    /**
     * regenerateRandomnumber
     * - Generates a 5 digits random number every time the screen is refreshed
     */
    private void regenerateRandomnumber() {
        int number = (int) Math.round(Math.random() * 100000);
        txtview_secretnumber.setText(String.valueOf(number));
    }

    /*
     * Defined to detect if the secret code match was success or failure.
     * If Success - then User is navigated to the scanner Result Screen
     * If Failed - The random number is regenerated
     */
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

    /**
     * Defines the clicking purpose of the button handler
     */
    private enum ScreenMode {
        PhoneVerify, SecretCode
    }
}
