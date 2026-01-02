package com.example.snapeats.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Cloudinary Configuration Class
 * Yeh class Cloudinary ke liye setup karti hai
 */
public class CloudinaryConfig {

    // Cloudinary account details (Yeh tumhare account ke honge)
    public static final String CLOUD_NAME = "dg0xe1oqm";  // Tumhara cloud name
    public static final String API_KEY = "463431159756792";        // Tumhara API key
    public static final String API_SECRET = "7UkM9EaAl6SwEdLfRjNZWG8TB8M";  // Tumhara API secret

    /**
     * Cloudinary ko configure karta hai
     */
    public Map getConfig() {
        Map config = new HashMap();
        config.put("cloud_name", CLOUD_NAME);
        config.put("api_key", API_KEY);
        config.put("api_secret", API_SECRET);
        return config;
    }
}