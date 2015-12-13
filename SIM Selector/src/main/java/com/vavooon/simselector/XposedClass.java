package com.vavooon.simselector;

import android.app.AndroidAppHelper;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
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

	private ArrayList<Integer> simIdList = new ArrayList<>();
	private ArrayList<Pattern> patternsList = new ArrayList<>();
	private void generatePatterns(Cursor cursor) {
		simIdList.clear();
		patternsList.clear();

		while (cursor.moveToNext()) {
			int simId = cursor.getInt(cursor.getColumnIndex(RulesContentProvider.RULE_SIMID));
			String patternString = cursor.getString(cursor.getColumnIndex(RulesContentProvider.RULE_TEXT));
			patternString = patternString.replace("#", ".").replace(",", "|").replace("+", "\\+");
			Pattern p = Pattern.compile(patternString);

			simIdList.add(simId);
			patternsList.add(p);
		}
	}

	public PhoneAccountHandle getPhoneAccountHandleForNumber(Context context, String uri) {
		TelecomManager telecomManager = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
		List<PhoneAccountHandle> availablePhoneAccountHandles = telecomManager.getCallCapablePhoneAccounts();
		Matcher matcher;

		for (int i = 0; i<patternsList.size(); i++) {
			matcher = patternsList.get(i).matcher(uri);
			Log.d(TAG, "Trying pattern: \"" + patternsList.get(i).pattern()+"\"");
			if(matcher.find())
			{
				Log.d(TAG, "Suitable pattern: " + patternsList.get(i).pattern());
				return availablePhoneAccountHandles.get(simIdList.get(i));
			}
		}
		Log.d(TAG, "No suitable patterns was found");
		return null;
	}


	public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
		if (lpparam.packageName.equals(PKGNAME_TELECOM)) {

			findAndHookMethod("com.android.server.telecom.components.UserCallIntentProcessor", lpparam.classLoader, "processOutgoingCallIntent", Intent.class, String.class, boolean.class, new XC_MethodHook() {
				@Override
				protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
					Intent intent = (Intent)param.args[0];
					Uri handle = intent.getData();
					String phoneNumber = handle.getSchemeSpecificPart().replaceAll("\\s+", "");

					Context context = AndroidAppHelper.currentApplication();

					String URL = "content://com.vavooon.simselector/rules";
					Uri friends = Uri.parse(URL);

					Cursor rulesList = context.getContentResolver().query(friends, null, null, null, null);
					generatePatterns(rulesList);
					PhoneAccountHandle p = getPhoneAccountHandleForNumber(context, phoneNumber);
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
