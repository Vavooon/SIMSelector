package com.vavooon.simselector;

import android.graphics.drawable.Icon;

/**
 * Created by Vavooon on 13.10.2015.
 */
public class RowItem {
    private int id;
    private Icon icon;
    private int simId;
    private String title;
    private String simName;

    public RowItem(int id, Icon icon, int simId, String title, String simName) {
        this.id = id;
        this.icon = icon;
        this.simId = simId;
        this.simName = simName;
        this.title = title;
    }

    public Icon getIcon() {
        return icon;
    }
    public String getTitle() {
        return title;
    }
    public int getId() {
        return id;
    }
    public int getSimId() {
        return simId;
    }
    public String getSimName() {
        return simName;
    }
    public String getRuleString() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    @Override
    public String toString() {
        return title;
    }
}