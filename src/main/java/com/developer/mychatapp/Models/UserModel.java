package com.developer.mychatapp.Models;

public class UserModel {
    String id;
    String name;
    String email;
    String url;
    String status;
    String number;

    public UserModel(String id, String name, String email,String url,String status,String number) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.url=url;
        this.status=status;
        this.number=number;

    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public UserModel(String status){
        this.status=status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
