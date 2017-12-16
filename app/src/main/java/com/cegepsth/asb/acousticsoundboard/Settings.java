package com.cegepsth.asb.acousticsoundboard;

/**
 * Created by Gabriele Sarti on 24-11-17.
 */

public class Settings {
    private int mId;
    private int mWidgetSoundKey;

    public int getId() { return mId; }
    public int getFavoriteSong(){
        return mWidgetSoundKey;
    }

    public void setId(int id) {mId = id;}
    public void setFavoriteSong(int key) {mWidgetSoundKey = key; }
}
