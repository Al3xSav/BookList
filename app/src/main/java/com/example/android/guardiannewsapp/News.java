package com.example.android.guardiannewsapp;

public class News {
    private String mSection;
    private String mTitle;
    private String mType;
    private String mDate;
    private String mUrl;

    public News(String section, String title, String type, String date, String url) {
        this.mSection = section;
        this.mTitle = title;
        this.mType = type;
        this.mDate = date;
        this.mUrl = url;
    }

    public String getSection() {
        return mSection;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getType() {
        return mType;
    }

    public String getDate() {
        return mDate;
    }

    public String getUrl() {
        return mUrl;
    }
}
