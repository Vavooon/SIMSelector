package com.vavooon.dualsimdialer;

import android.app.AndroidAppHelper;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
    final static String PKGNAME_TELECOM = "com.android.server.telecom";

    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (lpparam.packageName.equals(PKGNAME_TELECOM)) {

            findAndHookMethod("com.android.server.telecom.components.UserCallIntentProcessor", lpparam.classLoader, "processOutgoingCallIntent", Intent.class, String.class, boolean.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    /*Log.e(TAG, "BEEFORES");
                    Intent intent = (Intent)param.args[0];
                    Uri handle = intent.getData();
                    //Log.d(TAG, observer.toString());
                    String phoneNumber = handle.getSchemeSpecificPart().replaceAll("\\s+","");
                    Context context = AndroidAppHelper.currentApplication();
                    rulesListInstance = getCallRulesListInstance(context);
                    rulesListInstance.loadData(readFile(RULES_FILE_NAME));
                    PhoneAccountHandle p = rulesListInstance.getPhoneAccountHandleForNumber(phoneNumber);
                    Log.e(TAG, rulesListInstance.toString());
                    intent.putExtra("android.telecom.extra.PHONE_ACCOUNT_HANDLE", p);
*/
                    String URL = "content://com.vavooon.dualsimdialer/rules";
                    Uri friends = Uri.parse(URL);

                    Context context = AndroidAppHelper.currentApplication();
                    Cursor rulesList = context.getContentResolver().query(friends, null, null, null, null);
                    Log.d(TAG, rulesList.toString());
                    while (rulesList.moveToNext()) {
                        Log.d(TAG, rulesList.getString(1));
                    }
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                }
            });
            Log.e(TAG, "we are in our app");
        }
    }
}
