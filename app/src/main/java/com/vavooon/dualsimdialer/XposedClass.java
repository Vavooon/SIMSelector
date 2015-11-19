package com.vavooon.dualsimdialer;

import android.app.Activity;
import android.app.AndroidAppHelper;
import android.content.Context;
import android.os.Bundle;
import android.telecom.PhoneAccountHandle;
import android.util.Log;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import de.robv.android.xposed.XposedHelpers;
/**
 * Created by Vavooon on 18.11.2015.
 */
public class XposedClass implements IXposedHookLoadPackage {
    private static final String TAG = "xposed_debug";
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {

        //final CallRulesList rulesListInstance;
        if (lpparam.packageName.equals("com.google.android.dialer")) {

            findAndHookMethod("com.android.dialer.util.IntentUtil", lpparam.classLoader, "getCallIntent", String.class, PhoneAccountHandle.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    // this will be called before the clock was updated by the original method
                    Log.e(TAG, "BEEFORES");

                    Context context = AndroidAppHelper.currentApplication();
                    Log.e(TAG, context.toString());
                    CallRulesList rulesListInstance = new CallRulesList(null, context);

                    PhoneAccountHandle p = rulesListInstance.getPhoneAccountHandleForNumber((String) param.args[0]);
                    Log.e(TAG, rulesListInstance.toString());


                    param.args[1] = p;
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    // this will be called after the clock was updated by the original method
                }
            });
        }
/*
        if (lpparam.packageName.equals("com.vavooon.dualsimdialer")) {

            XposedBridge.log("we are in our app");

            findAndHookMethod("com.vavooon.dualsimdialer.MainActivity", lpparam.classLoader, "onCreate", Bundle.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    // this will be called after the clock was updated by the original method
                    Common.LAUNCHER_INSTANCE = (Activity)param.thisObject;
                    s="222";
                    Log.e(TAG, "Steal activity");
                }
            });

        }*/
    }
}
