package com.example.snapeats.models;

public class CategoriesModel {
    private String id;
    private String categoryImage;
    private String categoryName;

    public CategoriesModel(String id,String image, String name) {
        this.id = id;
        this.categoryImage = image;
        this.categoryName = name;
    }

    public CategoriesModel() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategoryImage() {
        return categoryImage;
    }

    public void setCategoryImage(String categoryImage) {
        this.categoryImage = categoryImage;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
