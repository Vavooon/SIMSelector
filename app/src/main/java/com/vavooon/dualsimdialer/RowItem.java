package com.vavooon.dualsimdialer;

/**
 * Created by Vavooon on 13.10.2015.
 */
public class RowItem {
    private int id;
    private int imageId;
    private String title;
    private boolean isAddButton;

    public RowItem(int id, int imageId, String title) {
        this.id = id;
        this.imageId = imageId;
        this.title = title;
    }

    public RowItem(int id, int imageId, String title, boolean isAddButton) {
        this.id = id;
        this.imageId = imageId;
        this.title = title;
        this.isAddButton = isAddButton;
    }
    public int getImageId() {
        return imageId;
    }
    public boolean isAddButton() {return isAddButton;}
    public void setImageId(int imageId) {
        this.imageId = imageId;
    }
    public String getTitle() {
        return title;
    }
    public int getId() {
        return id;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    @Override
    public String toString() {
        return title;
    }
}