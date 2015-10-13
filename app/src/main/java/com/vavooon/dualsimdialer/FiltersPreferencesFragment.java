package com.vavooon.dualsimdialer;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.util.Log;

import java.util.List;

/**
 * Created by Vavooon on 12.10.2015.
 */
public class FiltersPreferencesFragment extends PreferenceFragment {
    CallRulesList rulesListInstance;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Activity a = getActivity();
        PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(a);
        setPreferenceScreen(screen);
        Preference.OnPreferenceClickListener ruleOnClickHandler= new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                Log.e("FF", "22");
                return true;
            }
        };

        rulesListInstance = CallRulesList.getInstance();
        Log.e("rulesList", Integer.toString(rulesListInstance.size()));
        for (int i = 0; i < rulesListInstance.size(); i++) {
            CallRule rule = rulesListInstance.get(i);
            Preference preference = new Preference(a);
            getContext();
            preference.setTitle(rule.ruleString);
            //preference.setIcon(R.mipmap.ic_menu_add);
            preference.setOnPreferenceClickListener(ruleOnClickHandler);
            preference.setFragment(RulePreferenceFragment.class.getName());
            screen.addPreference(preference);
        }
        screen.addPreference(newAddAccountPreference(a));
    }

    private Preference newAddAccountPreference(Context context) {
        Preference preference = new Preference(context);
        preference.setTitle("Add filter");
        preference.setIcon(R.mipmap.ic_menu_add);
        //preference.setOnPreferenceClickListener(this);
        //preference.setOrder(ORDER_NEXT_TO_LAST);
        return preference;
    }




}
