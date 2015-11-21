package com.vavooon.dualsimdialer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Vavooon on 20.11.2015.
 */

public class AutorunReceiver extends BroadcastReceiver {

    private static final String TAG = "xposed_debug";
    @Override
    public void onReceive(Context context, Intent i) {
/*
        SharedPreferences sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        Intent intent = new Intent();
        intent.setAction("com.google.android.dialer.UPDATE_RULES");
        String rulesString = sharedPreferences.getString("rulesList", null);
        intent.putExtra("rules", rulesString);
        context.sendBroadcast(intent);
        }*/


        Log.e(TAG, "Broadcast receiver runned");
        File myCommands = new File(Environment.getDataDirectory() + "/data/com.vavooon.dualsimdialer", "commands");


        if(!myCommands.exists()) {

            try {
                myCommands.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        myCommands.setReadable(true, false);
        myCommands.setWritable(true, false);

        Log.e(TAG, myCommands.getAbsolutePath());
        try {
            FileWriter fw = new FileWriter(myCommands);
            fw.write("red");
            fw.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
