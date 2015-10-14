package com.vavooon.dualsimdialer;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import java.util.ArrayList;

/**
 * Created by Vavooon on 13.10.2015.
 */
public class CallRulesList extends Application {
    private ArrayList<CallRule> rulesList = new ArrayList<CallRule>();
    private static CallRulesList singleton = null;
    private Context context;
    SharedPreferences prefs;

    public CallRulesList (Context c) {
        context = c;
        singleton = this;
        prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        loadData();
    }

    public CallRulesList () {
        prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        singleton = this;
        loadData();
    }

    public static CallRulesList getInstance() {
        if(singleton == null) {
            throw new Error("Please initialize singleton with proper context");
        }
        return singleton;
    }

    void addRule(CallRule rule) {
        rulesList.add(rule);
    }

    void updateRule(int id, CallRule rule) {
        rulesList.set(id, rule);
    }

    void removeRule (CallRule rule) {
        rulesList.remove(rule);
    }

    void removeRule (int index) {
        rulesList.remove(index);
    }

    int size () {
        return rulesList.size();
    }

    CallRule get (int index) {
        return rulesList.get(index);
    }

    private void loadData () {
        String value = prefs.getString("rulesList", null);
        if (value != null) {
            Log.e("loadData", value);
            String[] rules = value.split("\\|");

            for (int i = 0; i<rules.length; i++) {
                Log.e("loadDataRule"+i, rules[i]);
                String[] rule = rules[i].split("\\:");
                int cardId = Integer.parseInt(rule[0]);
                String ruleString = rule[1];
                rulesList.add( new CallRule(cardId, ruleString));
            }
        }
    }

    public void saveData () {
        SharedPreferences.Editor editor = prefs.edit();
        String encodedRules = "";
        CallRule rule;
        for (int i = 0; i<rulesList.size(); i++) {
            rule = rulesList.get(i);
            encodedRules += "" + rule.cardId + ":" + rule.ruleString + "|";
        }
        editor.putString("rulesList", encodedRules.replaceFirst(".$",""));
        editor.commit();
    }
}
