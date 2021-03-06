package com.example.a24168.myapplication.kitchen.like.entity;

import java.io.Serializable;

public class Show implements Serializable {

    private String image;
    private String title;
    private String touxiang;
    private String nicheng;
    private int dianzan;
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public Show(String image, String title, String touxiang, String nicheng, int dianzan, int id) {
        this.image = image;
        this.title = title;
        this.touxiang = touxiang;
        this.nicheng = nicheng;
        this.dianzan = dianzan;
        this.id = id;
    }

    public String getTouxiang() {
        return touxiang;
    }

    public void setTouxiang(String touxiang) {
        this.touxiang = touxiang;
    }

    public String getNicheng() {
        return nicheng;
    }

    public void setNicheng(String nicheng) {
        this.nicheng = nicheng;
    }

    public int getDianzan() {
        return dianzan;
    }

    public void setDianzan(int dianzan) {
        this.dianzan = dianzan;
    }

    public Show(String image, String title) {
        this.image = image;
        this.title = title;
    }

    public Show() {
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
