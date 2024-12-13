package com.example.myapplication.data.source.remote.firebase;

import static com.example.myapplication.data.source.remote.firebase.ValidatorUser.isDobValid;
import static com.example.myapplication.data.source.remote.firebase.ValidatorUser.isEmailValid;
import static com.example.myapplication.data.source.remote.firebase.ValidatorUser.isNameValid;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;

public class UserEntity {
    private String id;
    private String name;
    private String image;
    private String email;
    private String dob;
    private int sex;

    public UserEntity() {

    }

    public UserEntity(String id, String name, String image, String email, String dob, int sex) {
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


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getImage(){
        return this.image;
    }
    public void setImage(String image){
       this.image = image;
    }



    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    @Override
    public String toString() {
        return "User{" +
                "image='" + "image" + '\'' +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", dob='" + dob + '\'' +
                ", sex='" + sex + '\'' +
                '}';
    }
    public static String convertUserEntityToString(UserEntity user) {
        Gson gson = new Gson();
        return gson.toJson(user);
    }
    public static UserEntity convertStringToUserEntity(String userJson) {
        if(userJson.isEmpty()) return null;
        Gson gson = new Gson();
        return gson.fromJson(userJson, UserEntity.class);
    }



}
