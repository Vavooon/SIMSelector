package com.vavooon.simselector;

/**
 * Created by Vavooon on 06.12.2015.
 */



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Bundle;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.text.TextUtils;
import android.util.Log;

public class RulesContentProvider extends ContentProvider {


	private static final String TAG = "xposed_debug";

	static final String DB_NAME = "rules";
	static final int DB_VERSION = 1;

	static final String AUTHORITY = "com.vavooon.simselector";
	static final String CONTACT_PATH = "rules";


	public static final Uri CONTENT_URI = Uri.parse("content://"
		+ AUTHORITY + "/" + CONTACT_PATH);
	static final int URI_CONTACTS = 1;

	static final int URI_CONTACTS_ID = 2;


	DBHelper dbHelper;
	SQLiteDatabase db;

	static final String RULE_TABLE = "RULES";
	static final String RULE_ID = "_id";
	static final String RULE_SIMID = "simid";
	static final String RULE_TEXT = "text";

	// Скрипт создания таблицы
	static final String DB_CREATE = "create table " + RULE_TABLE + "("
			+ RULE_ID + " integer primary key autoincrement, "
			+ RULE_SIMID + " integer, " + RULE_TEXT + " text" + ");";

	private static final UriMatcher uriMatcher;
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(AUTHORITY, CONTACT_PATH, URI_CONTACTS);
		uriMatcher.addURI(AUTHORITY, CONTACT_PATH + "/#", URI_CONTACTS_ID);
	}



	private ArrayList<Integer> simIdList = new ArrayList<>();
	private ArrayList<Pattern> patternsList = new ArrayList<>();

	@Override
	public boolean onCreate() {
		Context context = getContext();
		dbHelper = new DBHelper(context);
		generatePatterns();
		return true;
	}

	public Cursor query(Uri uri, String[] projection, String selection,
											String[] selectionArgs, String sortOrder) {

		switch (uriMatcher.match(uri)) {
			case URI_CONTACTS:
				break;
			case URI_CONTACTS_ID:
				String id = uri.getLastPathSegment();
				if (TextUtils.isEmpty(selection)) {
					selection = RULE_ID + " = " + id;
				} else {
					selection = selection + " AND " + RULE_ID + " = " + id;
				}
				break;
			default:
				throw new IllegalArgumentException("Wrong URI: " + uri);
		}
		db = dbHelper.getWritableDatabase();
		Cursor cursor = db.query(RULE_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
		cursor.setNotificationUri(getContext().getContentResolver(), CONTENT_URI);
		return cursor;
	}


	public Uri insert(Uri uri, ContentValues values) {

		db = dbHelper.getWritableDatabase();
		long rowID = db.insert(RULE_TABLE, null, values);
		Uri resultUri = ContentUris.withAppendedId(CONTENT_URI, rowID);
		getContext().getContentResolver().notifyChange(resultUri, null);
		generatePatterns();
		return resultUri;
	}

	public int update(Uri uri, ContentValues values, String selection,
										String[] selectionArgs) {
		Log.d(TAG, uri.toString());
		switch (uriMatcher.match(uri)) {
			case URI_CONTACTS:

				break;
			case URI_CONTACTS_ID:
				String id = uri.getLastPathSegment();
				if (TextUtils.isEmpty(selection)) {
					selection = RULE_ID + " = " + id;
				} else {
					selection = selection + " AND " + RULE_ID + " = " + id;
				}
				break;
			default:
				throw new IllegalArgumentException("Wrong URI: " + uri);
		}

		db = dbHelper.getWritableDatabase();
		int cnt = db.update(RULE_TABLE, values, selection, selectionArgs);
		getContext().getContentResolver().notifyChange(uri, null);
		generatePatterns();
		return cnt;
	}


	public int delete(Uri uri, String selection, String[] selectionArgs) {
		switch (uriMatcher.match(uri)) {
			case URI_CONTACTS:
				break;
			case URI_CONTACTS_ID:
				String id = uri.getLastPathSegment();
				if (TextUtils.isEmpty(selection)) {
					selection = RULE_ID + " = " + id;
				} else {
					selection = selection + " AND " + RULE_ID + " = " + id;
				}
				break;
			default:
				throw new IllegalArgumentException("Wrong URI: " + uri);
		}
		db = dbHelper.getWritableDatabase();
		int cnt = db.delete(RULE_TABLE, selection, selectionArgs);
		getContext().getContentResolver().notifyChange(uri, null);
		generatePatterns();
		return cnt;
	}

	public String getType(Uri uri) {
			return null;
	}

	@Override
	public Bundle call(String method, String arg, Bundle extras) {
		if ("getPhoneAccountId".equals(method) && arg != null) {
			Bundle bundle = new Bundle();
			bundle.putInt("id", getPhoneAccountId(arg));
			return bundle;
		}
		return(null);
	}


	private class DBHelper extends SQLiteOpenHelper {

		public DBHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
		}

		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DB_CREATE);
		}

		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		}
	}

	private void generatePatterns() {
		simIdList.clear();
		patternsList.clear();String URL = "content://com.vavooon.simselector/rules";
		Uri uri = Uri.parse(URL);
		Cursor cursor = query(uri, null, null, null, null);
		while (cursor.moveToNext()) {
			int simId = cursor.getInt(cursor.getColumnIndex(RulesContentProvider.RULE_SIMID));
			String patternString = cursor.getString(cursor.getColumnIndex(RulesContentProvider.RULE_TEXT));
			patternString = patternString.replace("#", ".").replace(",", "|").replace("+", "\\+");
			Pattern p = Pattern.compile(patternString);

			simIdList.add(simId);
			patternsList.add(p);
		}
	}

	private int getPhoneAccountId(String phoneNumber) {
		Matcher matcher;
		phoneNumber = phoneNumber.replaceAll("\\s+", "");

		for (int i = 0; i<patternsList.size(); i++) {
			matcher = patternsList.get(i).matcher(phoneNumber);
			Log.d(TAG, "Trying pattern: \"" + patternsList.get(i).pattern()+"\"");
			if(matcher.find())
			{
				Log.d(TAG, "Suitable pattern: " + patternsList.get(i).pattern());
				return simIdList.get(i);
			}
		}
		Log.d(TAG, "No suitable patterns was found");
		return -1;
	}

}
