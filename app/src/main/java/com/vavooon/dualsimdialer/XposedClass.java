package com.vavooon.dualsimdialer;

import android.app.Activity;
import android.app.AndroidAppHelper;
import android.app.Application;
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
import android.provider.Settings;
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
public class XposedClass extends Application implements IXposedHookLoadPackage {
    private static final String TAG = "xposed_debug";
    CallRulesList callRulesListInstance = null;
    public static FileObserver observer = null;
    private CallRulesList getCallRulesListInstance(Context c) {
        if (callRulesListInstance == null) {
            callRulesListInstance = new CallRulesList(c, null);
        }
        return callRulesListInstance;
    }

    public static String readFile(File file) throws Exception {
        FileInputStream fin = new FileInputStream(file);
        BufferedReader reader = new BufferedReader(new InputStreamReader(fin));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        fin.close();
        return sb.toString();
    }


    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {

        if (lpparam.packageName.equals("com.google.android.dialer")) {

            observer = new FileObserver(Environment.getDataDirectory() + "/data/com.vavooon.dualsimdialer/commands") {

                @Override
                public void onEvent(int event, String file) {
                    Log.d(TAG, "File event " + event);
                    if (event == FileObserver.MODIFY) {
                        Log.d(TAG, "File has been modified");
                        File rulesFile = new File(Environment.getDataDirectory() + "/data/com.vavooon.dualsimdialer", "commands");
                        try {
                            String rulesString = readFile(rulesFile);
                            Log.d(TAG, rulesString);
                        }
                        catch (Exception e) {
                            Log.d(TAG, e.toString());
                        }
                    }
                }
            };
            observer.startWatching(); //START OBSERVING
            MainActivity.o = observer;
            Log.d(TAG, "Try to read fuckin file");
            File rulesFile = new File(Environment.getDataDirectory() + "/data/com.vavooon.dualsimdialer", "commands");
            try {
                String rulesString = readFile(rulesFile);
                Log.d(TAG, rulesString);
            }
            catch (Exception e) {
                Log.d(TAG, e.toString());
            }

            //Log.e(TAG, ret);
            findAndHookMethod("com.android.dialer.util.IntentUtil", lpparam.classLoader, "getCallIntent", String.class, PhoneAccountHandle.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    Log.e(TAG, "BEEFORES");
                    Context context = AndroidAppHelper.currentApplication();
                    CallRulesList rulesListInstance = getCallRulesListInstance(context);


                    Log.d(TAG, observer.toString());

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
