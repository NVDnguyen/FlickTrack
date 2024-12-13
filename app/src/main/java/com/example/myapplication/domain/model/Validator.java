package com.example.myapplication.domain.model;


import android.text.TextUtils;

import java.util.regex.Pattern;

public class Validator {

    public static boolean isNameValid(String name) {
        return !TextUtils.isEmpty(name) && name.length() >= 3;
    }

    public static boolean isEmailValid(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return !TextUtils.isEmpty(email) && Pattern.matches(emailPattern, email);
    }

    public static boolean isDobValid(String dob) {
        // (dd/MM/yyyy) or (yyyy-MM-dd)
        String dobPattern = "\\d{2}/\\d{2}/\\d{4}|\\d{4}-\\d{2}-\\d{2}";
        return !TextUtils.isEmpty(dob) && Pattern.matches(dobPattern, dob);
    }

    public static boolean isSexValid(boolean sex) {
        return true;
    }
}

