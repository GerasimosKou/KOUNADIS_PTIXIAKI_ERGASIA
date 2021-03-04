package com.example.ptixiakiergasia;

import java.util.Date;

public class Upload {
    private String mImageUrl;
    private Date mDate;
    private String mKey;

    public Upload() {
        //empty constructor needed
    }

    public Upload(String imageUrl, Date date) {

        mImageUrl = imageUrl;
        mDate = date;
    }


    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }

    public Date getDate(){return mDate;}

    public void setDate(Date date) {mDate = date;}

    public String getKey() {
        return mKey;
    }

    public void setKey(String key){
        mKey = key;
    }
}