package com.example.snapeats.models;


import java.io.Serializable;
import java.util.Objects;

public class Food_Item_Model implements Serializable {
    private String id;
    public int food_image;
    public String food_name;
    public String food_restaurant_name;
    public String descdescriptionprice;
    public String price;


    public int base_price;
    public int cart_count;

    private boolean isPopular;
    private boolean isRecommended;
    private boolean isInWishlist;
    private boolean isInCart;

    public int getFood_image() {
        return food_image;
    }

    public void setFood_image(int food_image) {
        this.food_image = food_image;
    }

    public int getCart_count() {
        return cart_count;
    }

    public void setCart_count(int cart_count) {
        this.cart_count = cart_count;
    }


    public void setBase_price(int base_price) {
        this.base_price = base_price;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getFood_restaurant_name() {
        return food_restaurant_name;
    }

    public void setFood_restaurant_name(String food_restaurant_name) {
        this.food_restaurant_name = food_restaurant_name;
    }

    public String getFood_name() {
        return food_name;
    }

    public void setFood_name(String food_name) {
        this.food_name = food_name;
    }

    public Food_Item_Model(int food_image, String food_name, String food_restaurant_name, String price) {
        this.food_image = food_image;
        this.food_name = food_name;
        this.food_restaurant_name = food_restaurant_name;
        this.price = price;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Food_Item_Model)) return false;
        Food_Item_Model other = (Food_Item_Model) obj;
        return food_name.equals(other.food_name) &&
                food_restaurant_name.equals(other.food_restaurant_name) &&
                price.equals(other.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(food_name, food_restaurant_name, price);
    }

}
