package com.vavooon.simselector;

import android.app.AndroidAppHelper;
import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	final static String URL = "content://com.vavooon.simselector/rules";
	List<PhoneAccountHandle> availablePhoneAccountHandles;

	public PhoneAccountHandle getPhoneAccountHandleById(Context context, int id) {
		if (availablePhoneAccountHandles == null) {
			TelecomManager telecomManager = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
			availablePhoneAccountHandles = telecomManager.getCallCapablePhoneAccounts();
		}
		return availablePhoneAccountHandles.get(id);
	}


	public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
		if (lpparam.packageName.equals(PKGNAME_TELECOM)) {

			findAndHookMethod("com.android.server.telecom.components.UserCallIntentProcessor", lpparam.classLoader, "processOutgoingCallIntent", Intent.class, String.class, boolean.class, new XC_MethodHook() {
				@Override
				protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
					Intent intent = (Intent)param.args[0];
					Uri handle = intent.getData();
					String phoneNumber = handle.getSchemeSpecificPart();

					Context context = AndroidAppHelper.currentApplication();
					Uri rulesUri = Uri.parse(URL);
					Bundle returnedBundle = context.getContentResolver().call(rulesUri, "getPhoneAccountId", phoneNumber, null);
					PhoneAccountHandle p = getPhoneAccountHandleById(context, returnedBundle.getInt("id"));
					intent.putExtra("android.telecom.extra.PHONE_ACCOUNT_HANDLE", p);
				}

				@Override
				protected void afterHookedMethod(MethodHookParam param) throws Throwable {
				}
			});
			Log.e(TAG, "we are in our app");
		}
	}
}
