package com.example.myapplication.domain.model;

import static com.example.myapplication.data.source.remote.firebase.ValidatorUser.isDobValid;
import static com.example.myapplication.data.source.remote.firebase.ValidatorUser.isEmailValid;
import static com.example.myapplication.data.source.remote.firebase.ValidatorUser.isNameValid;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;

public class User {
    private String id;
    private String name;
    private Bitmap image;
    private String email;
    private String dob;
    private int sex;

    public User() {
        this.name = "Nguyen Van A";
        this.email = "hello123@example.com";
        this.dob = "11/11/1111";
        this.sex = 1;
    }
    public User(User aUser){
        this.id = aUser.getId();
        this.name = aUser.getName();
        this.image = aUser.getImage();
        this.email = aUser.getEmail();
        this.dob = aUser.getDob();
        this.sex = aUser.getSex();
    }
    public User(String id, String name, Bitmap image, String email, String dob, int sex) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.email = email;
        this.dob = dob;
        this.sex = sex;
    }

    public void validateUserData() {
        if (!isNameValid(name)) {
            throw new IllegalArgumentException("Invalid name");
        }
        if (!isEmailValid(email)) {
            throw new IllegalArgumentException("Invalid email");
        }
        if (!isDobValid(dob)) {
            throw new IllegalArgumentException("Invalid date of birth");
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }




    @Override
    public String toString() {
        String  i = image==null?"null":"not null";
        return "User{" +
                ", image='"+  i + '\'' +
                ", name='"+ name + '\'' +
                ", email='" + email + '\'' +
                ", dob='" + dob + '\'' +
                ", sex='" + sex + '\'' +
                '}';
    }

}
