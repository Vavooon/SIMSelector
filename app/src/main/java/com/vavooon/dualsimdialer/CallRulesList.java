package com.vavooon.dualsimdialer;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.robv.android.xposed.XSharedPreferences;

/**
 * Created by Vavooon on 13.10.2015.
 */
public class CallRulesList extends Application {
    private ArrayList<CallRule> rulesList = new ArrayList<CallRule>();
    private ArrayList<Pattern> patternsList = new ArrayList<Pattern>();
    private static CallRulesList singleton = null;
    private Context context;
    SharedPreferences prefs;
    private static final String TAG = "xposed_debug";

    private void generatePatterns() {
        patternsList.clear();
        if (rulesList.size()!=0) {
            Log.d(TAG, "Loaded regexps list:");
        }
        else {
            Log.d(TAG, "No regexps was found");
        }
        for (int i = 0; i<rulesList.size(); i++) {
            String patternString = rulesList.get(i).getRuleString();
            patternString = patternString.replace("#", ".").replace(",", "|").replace("+", "\\+");
            Pattern p = Pattern.compile(patternString);
            Log.d(TAG, patternString);
            patternsList.add(p);
        }
    }

    public CallRulesList (Context c, SharedPreferences p) {
        context = c;
        singleton = this;
        Log.e("xposed_debug", "init");
        if (p != null) {
            prefs = p;
        }
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
        generatePatterns();
        saveData();
    }

    void updateRule(int id, CallRule rule) {
        rulesList.set(id, rule);
        generatePatterns();
        saveData();
    }

    void removeRule (CallRule rule) {
        rulesList.remove(rule);
        generatePatterns();
        saveData();
    }

    void removeRule (int index) {
        rulesList.remove(index);
        generatePatterns();
        saveData();
    }

    int size () {
        return rulesList.size();
    }

    CallRule get (int index) {
        return rulesList.get(index);
    }

    public void loadData (String value) {
        if (value != null) {
            String[] rules = value.replaceAll("\\r|\\n", "").split("\\|");
            rulesList.clear();
            for (int i = 0; i < rules.length; i++) {
                String[] rule = rules[i].split("\\:");
                int cardId = Integer.parseInt(rule[0]);
                String ruleString = rule[1];
                rulesList.add(new CallRule(cardId, ruleString));
            }
            generatePatterns();
        }
    }

    private void loadData () {
        if (prefs != null ) {
            String value = prefs.getString("rulesList", null);
            loadData(value);
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
        encodedRules  = encodedRules.replaceFirst(".$", "");
        editor.putString("rulesList", encodedRules);
        editor.commit();
        saveDataToFile(encodedRules);
    }

    public PhoneAccountHandle getPhoneAccountHandleForNumber(String uri) {
        TelecomManager telecomManager = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
        List<PhoneAccountHandle> availablePhoneAccountHandles = telecomManager.getCallCapablePhoneAccounts();
        Matcher matcher;

        Log.d(TAG, ""+patternsList.size());
        for (int i = 0; i<patternsList.size(); i++) {
            matcher = patternsList.get(i).matcher(uri);
            Log.d(TAG, "Trying pattern: \"" + patternsList.get(i).pattern()+"\"");
            if(matcher.find())
            {
                Log.d(TAG, "Suitable pattern: " + patternsList.get(i).pattern());
                return availablePhoneAccountHandles.get(rulesList.get(i).cardId);
            }
        }
        Log.d(TAG, "No suitable patterns was found");
        return null;
    }

    private void saveDataToFile(String rules) {
        File myCommands = new File(Environment.getDataDirectory() + "/data/com.vavooon.dualsimdialer", "commands");


        if(!myCommands.exists()) {

            try {
                myCommands.createNewFile();
            } catch (IOException e) {
                Log.d(TAG, e.toString());
            }

        }

        myCommands.setReadable(true, false);
        myCommands.setWritable(true, false);

        Log.e(TAG, myCommands.getAbsolutePath());
        try {
            FileWriter fw = new FileWriter(myCommands);
            fw.write(rules);
            fw.close();
        } catch(Exception e) {
            Log.d(TAG, e.toString());
        }
    }
}
