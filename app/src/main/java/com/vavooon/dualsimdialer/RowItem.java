package com.vavooon.dualsimdialer;

/**
 * Created by Vavooon on 13.10.2015.
 */
public class RowItem {
    private int id;
    private int imageId;
    private int simId;
    private String title;

    public RowItem(int id, int imageId, int simId, String title) {
        this.id = id;
        this.imageId = imageId;
        this.simId = simId;
        this.title = title;
    }

    public int getImageId() {
        return imageId;
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