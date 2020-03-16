package com.developer.mychatapp.Models;

import java.io.Serializable;

public class SingleUserModel {
    String username;
    String id;
    String image;
    String status;
    String token;
    String number;

    public SingleUserModel(String username,String id,String image,String status,String number) {
        this.username = username;
        this.id = id;
        this.image = image;
        this.status=status;
        this.number = number;

    }

    public SingleUserModel(){

    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
