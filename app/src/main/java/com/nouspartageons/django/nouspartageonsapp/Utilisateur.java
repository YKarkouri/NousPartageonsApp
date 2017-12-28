package com.nouspartageons.django.nouspartageonsapp;

/**
 * Created by django on 28/12/17.
 */

public class Utilisateur {

    private String name;
    private String image;
    private String status;

    public Utilisateur() {
    }

    public Utilisateur(String name, String image, String status) {
        this.name = name;
        this.image = image;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getStatus() {
        return status;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
