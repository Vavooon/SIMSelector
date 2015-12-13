package com.vavooon.simselector;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.List;

/**
 * Created by Vavooon on 12.10.2015.
 */
public class MainActivity extends PreferenceActivity {
	final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		if (!checkPermission(this)) {
			requestPermission();
		}
	}

	@Override
	public void onBuildHeaders(List<Header> target) {
		Header rulesHeader = new Header();
		rulesHeader.title = getString(R.string.rules);
		rulesHeader.fragment = RulesListFragment.class.getName();

		target.add(rulesHeader);
//
//        Header othersOptionsHeader = new Header();
//        othersOptionsHeader.title = "Others";
//        target.add(othersOptionsHeader);
	}

	public static boolean checkPermission(Context c){
		return (ContextCompat.checkSelfPermission(c, Manifest.permission.CALL_PHONE)== PackageManager.PERMISSION_GRANTED);
	}

	private void requestPermission(){

		if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)){

			//Toast.makeText(context,"GPS permission allows us to access location data. Please allow in App Settings for additional functionality.",Toast.LENGTH_LONG).show();

		} else {

			ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, MY_PERMISSIONS_REQUEST_CALL_PHONE);
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode,
																				 String permissions[], int[] grantResults) {
		switch (requestCode) {
			case MY_PERMISSIONS_REQUEST_CALL_PHONE: {
				// If request is cancelled, the result arrays are empty.
				if (grantResults.length > 0
					&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {

					// permission was granted, yay! Do the
					// contacts-related task you need to do.
				} else {

					// permission denied, boo! Disable the
					// functionality that depends on this permission.
				}
				return;
			}

			// other 'case' lines to check for other
			// permissions this app might request
		}
	}

	@Override
	protected boolean isValidFragment(String fragmentName) {
			return true;
	}

}
