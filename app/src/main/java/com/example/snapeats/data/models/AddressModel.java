package com.example.snapeats.data.models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class AddressModel {
    private String addressId;
    private String locationType; // Home, Work, Other
    private String fullName;
    private String phoneNumber;
    private String houseNumber;
    private String street;
    private String city;
    private String state;
    private String pinCode;
    private boolean isDefault;
//    private String timestamp;

    // Required empty constructor for Firebase
    public AddressModel() {
    }

    // Constructor with all fields
    public AddressModel(String addressId, String locationType, String fullName,
                        String phoneNumber, String houseNumber, String street,
                        String city, String state, String pinCode,
                        boolean isDefault) {
        this.addressId = addressId;
        this.locationType = locationType;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.houseNumber = houseNumber;
        this.street = street;
        this.city = city;
        this.state = state;
        this.pinCode = pinCode;
        this.isDefault = isDefault;
    }

    // Getters and Setters
    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    public String getLocationType() {
        return locationType;
    }

    public void setLocationType(String locationType) {
        this.locationType = locationType;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPinCode() {
        return pinCode;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }

    // Firebase works better with getter names that match field names
    public boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    // Alternative setter for convenience
    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    // Helper method to get complete address as string
    public String getCompleteAddress() {
        return houseNumber + ", " + street + ", " + city + ", " + state + " - " + pinCode;
    }

    // Helper method to get short address
    public String getShortAddress() {
        return houseNumber + ", " + street + ", " + city;
    }

    @Override
    public String toString() {
        return "AddressModel{" +
                "addressId='" + addressId + '\'' +
                ", locationType='" + locationType + '\'' +
                ", fullName='" + fullName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", houseNumber='" + houseNumber + '\'' +
                ", street='" + street + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", pinCode='" + pinCode + '\'' +
                ", isDefault=" + isDefault +
                '}';
    }
}