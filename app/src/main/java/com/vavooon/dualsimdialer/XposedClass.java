package com.vavooon.dualsimdialer;

import android.app.Activity;
import android.app.AndroidAppHelper;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileObserver;
import android.os.IBinder;
import android.telecom.PhoneAccountHandle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookConstructor;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import de.robv.android.xposed.XposedHelpers;
/**
 * Created by Vavooon on 18.11.2015.
 */
public class XposedClass implements IXposedHookLoadPackage {
    private static final String TAG = "xposed_debug";
    String test = "old";
    CallRulesList callRulesListInstance = null;
    FileObserver observer = null;
    private CallRulesList getCallRulesListInstance(Context c) {
        if (callRulesListInstance == null) {
            callRulesListInstance = new CallRulesList(c, null);
        }
        return callRulesListInstance;
    }

    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }


    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {

        //final CallRulesList rulesListInstance;
        if (lpparam.packageName.equals("com.google.android.dialer")) {


            final BroadcastReceiver b = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Log.e(TAG, "Got broadcast");
                    if (callRulesListInstance != null) {
                        callRulesListInstance.loadData(intent.getStringExtra("rules"));
                    }
                }
            };


/*
            File myCommands = new File(Environment.getDataDirectory() + "/data/com.vavooon.dualsimdialer", "commands");

            FileInputStream fin = new FileInputStream(myCommands);
            String ret = convertStreamToString(fin);
            //Make sure you close all streams.
            fin.close();
*/
            observer = new FileObserver(Environment.getDataDirectory() + "/data/com.vavooon.dualsimdialer/commands", FileObserver.MODIFY) {

                @Override
                public void onEvent(int event, String file) {
                    Log.e(TAG, "File event");
                }
            };
            observer.startWatching(); //START OBSERVING

            //Log.e(TAG, ret);
            findAndHookMethod("com.android.dialer.util.IntentUtil", lpparam.classLoader, "getCallIntent", String.class, PhoneAccountHandle.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    Log.e(TAG, "BEEFORES");
                    Context context = AndroidAppHelper.currentApplication();
                    CallRulesList rulesListInstance = getCallRulesListInstance(context);


                    context.registerReceiver(b, new IntentFilter("com.google.android.dialer.UPDATE_RULES"));


                    PhoneAccountHandle p = rulesListInstance.getPhoneAccountHandleForNumber((String) param.args[0]);
                    Log.e(TAG, rulesListInstance.toString());
                    param.args[1] = p;
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                }
            });
            Log.e(TAG, "we are in our app");
        }
    }
}
