package com.vavooon.dualsimdialer;

/**
 * Created by Vavooon on 13.10.2015.
 */
public class CallRule {
    int cardId;
    String ruleString;

    public CallRule () {

    }
    public CallRule(int id, String string) {
        cardId = id;
        ruleString = string;
    }

    public String getRuleString() {
        return ruleString;
    }
}
