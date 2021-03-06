package com.cegepsth.asb.acousticsoundboard;

public class Sound {

    private int mId;
    private int mDuration;
    private String mName;
    private String mPath;
    private byte[] mImage;

    public Sound(){}

    public Sound(String name, String path){
        mName = name;
        mPath = path;
    }

    public int getId() { return mId; }
    public int getDuration(){
        return mDuration;
    }
    public String getName(){
        return mName;
    }
    public String getPath(){
        return mPath;
    }
    public byte[] getImage() {return mImage; }

    public void setId(int id) {mId = id;}
    public void setDuration(int duration) {mDuration = duration; }
    public void setName(String name) {mName = name; }
    public void setPath(String path){
        mPath = path;
    }
    public void setImage(byte[] image) {mImage = image; }
}
