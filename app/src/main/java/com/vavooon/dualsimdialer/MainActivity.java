package com.vavooon.dualsimdialer;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.FileObserver;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.EditText;


import java.io.File;
import java.io.FileWriter;
import java.util.List;

public class MainActivity extends Activity {


    private static final String TAG = "list";
    List<PhoneAccountHandle> availablePhoneAccountHandles;
    TextView phoneNumberField;
    public CallRulesList rulesListInstance;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rulesListInstance = new CallRulesList(getApplication(), getSharedPreferences("settings", Context.MODE_PRIVATE));

        phoneNumberField = (EditText)findViewById(R.id.phoneNumberField);
        TelecomManager telecomManager =
                (TelecomManager) this.getSystemService(Context.TELECOM_SERVICE);
        availablePhoneAccountHandles = telecomManager.getCallCapablePhoneAccounts();


        FileObserver observer = new FileObserver(Environment.getDataDirectory() + "/data/com.vavooon.dualsimdialer/commands") {

            @Override
            public void onEvent(int event, String file) {
                Log.e(TAG, "File event catched in main app");
            }
        };
        observer.startWatching(); //START OBSERVING

        View.OnClickListener callButtonClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String uri = phoneNumberField.getText().toString();
                //startCall(Uri.parse("tel:" + uri), rulesListInstance.getPhoneAccountHandleForNumber(uri));
                Intent intent = new Intent();
                intent.setAction("com.google.android.dialer.UPDATE_RULES");
                intent.putExtra("rules", "0:(\\+38,38,8)0(63,73,93)#######|1:(\\+38,38,8)0(67,68,96,97,98)#######");
                sendBroadcast(intent);

                File myCommands = new File(Environment.getDataDirectory() + "/data/com.vavooon.dualsimdialer", "commands");
                myCommands.setReadable(true, false);
                myCommands.setWritable(true, false);

                Log.e(TAG, myCommands.getAbsolutePath());
                try {
                    FileWriter fw = new FileWriter(myCommands);
                    fw.write("rddded");
                    fw.close();
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        };

        LinearLayout callButtonsLayout = (LinearLayout)findViewById(R.id.callButtonsLayout);

        for (int i = 0; i < availablePhoneAccountHandles.size(); i++) {
            PhoneAccountHandle phoneAccountHandle = availablePhoneAccountHandles.get(i);
            Log.e(TAG, (String) telecomManager.getPhoneAccount(phoneAccountHandle).getLabel());

        }


        Button callButton = new Button(this);
        callButton.setText( "Call" );
        callButton.setOnClickListener( callButtonClick );
        callButtonsLayout.addView(callButton);
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item) {
        Intent i = new Intent(this, PreferencesActivity.class);
        startActivity(i);
        return true;
    }

    void startCall(Uri uri, PhoneAccountHandle phoneAccountHandle) {
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Intent.ACTION_CALL, uri);
            intent.putExtra("android.telecom.extra.PHONE_ACCOUNT_HANDLE", phoneAccountHandle);
            try {
                startActivity(intent);
            } catch (SecurityException e) {

            }
        }
    }


}
