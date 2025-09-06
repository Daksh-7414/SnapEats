package com.example.snapeats.models;


import com.google.firebase.database.PropertyName;

import java.io.Serializable;
import java.util.Objects;

public class Food_Item_Model implements Serializable {
    private String id;
    public String food_image;
    public String food_name;
    public String food_restaurant_name;
    public String description;
    public int price;
    public String category;
    public float rating;
    public int cart_count;
    public boolean isInWishlist;
    public boolean isInCart;
    public boolean isPopular;
    public boolean isRecommended;

    public Food_Item_Model() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFood_image() {
        return food_image;
    }

    public void setFood_image(String food_image) {
        this.food_image = food_image;
    }


    public String getFood_name() {
        return food_name;
    }

    public void setFood_name(String food_name) {
        this.food_name = food_name;
    }

    public String getFood_restaurant_name() {
        return food_restaurant_name;
    }

    public void setFood_restaurant_name(String food_restaurant_name) {
        this.food_restaurant_name = food_restaurant_name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getCart_count() {
        return cart_count;
    }

    public void setCart_count(int cart_count) {
        this.cart_count = cart_count;
    }

    public boolean isPopular() {
        return isPopular;
    }

    public void setPopular(boolean popular) {
        isPopular = popular;
    }

    public boolean isRecommended() {
        return isRecommended;
    }

    public void setRecommended(boolean recommended) {
        isRecommended = recommended;
    }

    public boolean isInWishlist() {
        return isInWishlist;
    }

    public void setInWishlist(boolean inWishlist) {
        isInWishlist = inWishlist;
    }

    public boolean isInCart() {
        return isInCart;
    }

    public void setInCart(boolean inCart) {
        isInCart = inCart;
    }

    //    @Override
//    public boolean equals(Object obj) {
//        if (this == obj) return true;
//        if (!(obj instanceof Food_Item_Model)) return false;
//        Food_Item_Model other = (Food_Item_Model) obj;
//        return food_name.equals(other.food_name) &&
//                food_restaurant_name.equals(other.food_restaurant_name) &&
//                price.equals(other.price);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(food_name, food_restaurant_name, price);
//    }

}
