package com.vavooon.dualsimdialer;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceActivity;

import java.util.List;

/**
 * Created by Vavooon on 12.10.2015.
 */
public class PreferencesActivity extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onBuildHeaders(List<Header> target) {
        Header displayOptionsHeader = new Header();
        displayOptionsHeader.title = "Filters";
        displayOptionsHeader.fragment = RulesListFragment.class.getName();

        target.add(displayOptionsHeader);

        Header othersOptionsHeader = new Header();
        othersOptionsHeader.title = "Others";
        target.add(othersOptionsHeader);
    }


    @Override
    protected boolean isValidFragment(String fragmentName) {
        return true;
    }

}
