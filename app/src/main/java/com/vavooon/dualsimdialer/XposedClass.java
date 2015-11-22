package com.vavooon.dualsimdialer;

import android.app.AndroidAppHelper;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.FileObserver;
import android.telecom.PhoneAccountHandle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

/**
 * Created by Vavooon on 18.11.2015.
 */
public class XposedClass extends Application implements IXposedHookLoadPackage {
    private static final String TAG = "xposed_debug";
    private final String RULES_FILE_NAME = Environment.getDataDirectory() + "/data/com.vavooon.dualsimdialer/commands";
    final static String PKGNAME_TELECOM = "com.android.server.telecom";
    CallRulesList rulesListInstance = null;
    public static FileObserver observer = null;
    private CallRulesList getCallRulesListInstance(Context c) {
        if (rulesListInstance == null) {
            rulesListInstance = new CallRulesList(c, null);
        }
        return rulesListInstance;
    }

    public static String readFile(String fileName)  {

        File file = new File(fileName);
        StringBuilder sb = new StringBuilder();
        try {
            FileInputStream fin = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fin));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            reader.close();
            fin.close();
        }
        catch (Exception e) {

        }
        return sb.toString();
    }


    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (lpparam.packageName.equals(PKGNAME_TELECOM)) {

            findAndHookMethod("com.android.server.telecom.components.UserCallIntentProcessor", lpparam.classLoader, "processOutgoingCallIntent", Intent.class, String.class, boolean.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    Log.e(TAG, "BEEFORES");
                    Intent intent = (Intent)param.args[0];
                    Uri handle = intent.getData();
                    //Log.d(TAG, observer.toString());
                    String phoneNumber = handle.getSchemeSpecificPart().replaceAll("\\s+","");
                    Log.d(TAG, handle.toString());
                    Log.d(TAG, phoneNumber);
                    PhoneAccountHandle p = rulesListInstance.getPhoneAccountHandleForNumber(phoneNumber);
                    Log.e(TAG, rulesListInstance.toString());
                    intent.putExtra("android.telecom.extra.PHONE_ACCOUNT_HANDLE", p);
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                }
            });


            findAndHookMethod("com.android.server.telecom.components.TelecomService", lpparam.classLoader, "initializeTelecomSystem", Context.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    Log.e(TAG, "initializeTelecomSystem");
                    Context context = AndroidAppHelper.currentApplication();
                    rulesListInstance = getCallRulesListInstance(context);
                    rulesListInstance.loadData(readFile(RULES_FILE_NAME));

                    observer = new FileObserver(Environment.getDataDirectory() + "/data/com.vavooon.dualsimdialer/commands") {

                        @Override
                        public void onEvent(int event, String file) {
                            Log.d(TAG, "File event " + event);
                            if (event == FileObserver.MODIFY) {
                                Log.d(TAG, "File has been modified");
                                rulesListInstance.loadData(readFile(RULES_FILE_NAME));
                            }
                        }
                    };
                    observer.startWatching(); //START OBSERVING
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                }
            });
            Log.e(TAG, "we are in our app");
        }
    }
}
