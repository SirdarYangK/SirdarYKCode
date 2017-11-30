package com.bjghhnt.app.treatmentdevice.bean;

import android.graphics.Bitmap;

/**
 * Created by Development_Android on 2016/12/12.
 */

public class MediaBean {
    public String mMediaTitle;
    public String mMediaDuration;
    public String mMediaArtist;
    public long mMedia_ID;
    public String mMediaDispalyName;
    public String mMediaData;
    public Bitmap album;

    @Override
    public String toString() {
        return "MediaBean{" +
                "mMediaTitle='" + mMediaTitle + '\'' +
                ", mMediaDuration='" + mMediaDuration + '\'' +
                ", mMediaArtist='" + mMediaArtist + '\'' +
                ", mMedia_ID='" + mMedia_ID + '\'' +
                ", mMediaDispalyName='" + mMediaDispalyName + '\'' +
                ", mMediaData='" + mMediaData + '\'' +
                '}';
    }
}
