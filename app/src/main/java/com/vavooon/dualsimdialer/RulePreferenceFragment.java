package com.vavooon.dualsimdialer;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.util.Log;

/**
 * Created by Vavooon on 13.10.2015.
 */
public class RulePreferenceFragment extends PreferenceFragment {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.rule_preferences);

        //Preference p = (Preference)getView().getParent();

        //Bundle args = getArguments();
        //EditTextPreference editField = (EditTextPreference)findPreference("rule_edit");
        //editField.setText((String)p.getTitle());
        Log.e("DD", "it works");
        PreferenceFragment parentFragment = (PreferenceFragment) getParentFragment();
    }

//    @Override
//    public View onCreateView(
//            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        return inflater.inflate(
//                com.android.internal.R.layout.preference_list_fragment, container, false);
//    }
}
