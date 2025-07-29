package com.example.snapeats.models;

public class Food_Cart_Model {
    public int food_image;
    public int cart_count;
    public String food_name, food_restaurant_name, price;
    public int base_price;

    public Food_Cart_Model(int food_image, int cart_count, String food_name, String food_restaurant_name, String price,int base_price) {
        this.food_image = food_image;
        this.cart_count = cart_count;
        this.food_name = food_name;
        this.food_restaurant_name = food_restaurant_name;
        this.price = price;
        this.base_price = base_price;
    }

    public int getFood_image() {
        return food_image;
    }

    public void setFood_image(int food_image) {
        this.food_image = food_image;
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

    public int getCart_count() {
        return cart_count;
    }

    public void setCart_count(int cart_count) {
        this.cart_count = cart_count;
    }
}
