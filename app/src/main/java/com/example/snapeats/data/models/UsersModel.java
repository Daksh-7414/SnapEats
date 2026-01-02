package com.example.snapeats.data.models;

import java.util.Map;

public class UsersModel {
    private String image;
    private String userName;
    private String emailAdd;
    private String userPassword;
    private String gender;
    private String phoneNo;
    private String loaction;


    public UsersModel() {
    }

    public UsersModel(String userName, String emailAdd, String userPassword) {
        this.userName = userName;
        this.emailAdd = emailAdd;
        this.userPassword = userPassword;
    }


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmailAdd() {
        return emailAdd;
    }

    public void setEmailAdd(String emailAdd) {
        this.emailAdd = emailAdd;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getLoaction() {
        return loaction;
    }

    public void setLoaction(String loaction) {
        this.loaction = loaction;
    }
}
