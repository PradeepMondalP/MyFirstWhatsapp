package com.example.myfirstwhatsapp;

public class Contacts {

    String name , status , images ;

    public Contacts() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public Contacts(String name, String status, String images) {
        this.name = name;
        this.status = status;
        this.images = images;
    }
}
