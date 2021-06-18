package com.md.sevensummitsfinal;

public class UploadImage {
    private String mName;
    private String mImageURL;

    public UploadImage(){

    }

    public UploadImage(String name, String imageURL){
        if(mName.trim().equals("")){
            mName = "not defined";
        }
        this.mName = name;
        this.mImageURL = imageURL;
    };

    public String getmImageURL() {
        return mImageURL;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public void setmImageURL(String mImageURL) {
        this.mImageURL = mImageURL;
    }
}