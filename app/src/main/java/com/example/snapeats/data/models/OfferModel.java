package com.example.snapeats.data.models;

public class OfferModel {
    int imageRes;
    String title;
    String subTitle;

    public OfferModel(int imageRes, String title, String subTitle) {
        this.imageRes = imageRes;
        this.title = title;
        this.subTitle = subTitle;
    }
    public int getImageRes() { return imageRes; }
    public String getTitle() { return title; }
    public String getSubTitle() { return subTitle; }
}