package com.summerlab.wishcards.Adapters;

import android.graphics.Rect;

/**
 * Created by sev_user on 1/7/2016.
 */
public class Sticker {

    private String path;

    public Rect getRect() {
        return rect;
    }

    public void setRect(Rect rect) {
        this.rect = rect;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    private Rect rect;

}
