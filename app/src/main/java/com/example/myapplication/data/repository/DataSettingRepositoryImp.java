package com.example.myapplication.data.repository;

import static com.example.myapplication.app.Constants.KEY_SP_ST_CATEGORY;
import static com.example.myapplication.app.Constants.KEY_SP_ST_RATE;
import static com.example.myapplication.app.Constants.KEY_SP_ST_SORT;
import static com.example.myapplication.app.Constants.KEY_SP_ST_YEAR;
import static com.example.myapplication.app.Constants.POPULAR;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.example.myapplication.domain.repository.DataSettingRepository;

import javax.inject.Inject;

public class DataSettingRepositoryImp implements DataSettingRepository {
    private final SharedPreferences sharedPreferences;
    @Inject
    public DataSettingRepositoryImp(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public String getCategory() {
        return sharedPreferences.getString(KEY_SP_ST_CATEGORY,POPULAR);
    }

    @Override
    public int getFilterByRating() {
        return sharedPreferences.getInt(KEY_SP_ST_RATE,0);
    }

    @Override
    public int getFilterByYear() {
        return Integer.parseInt(sharedPreferences.getString(KEY_SP_ST_YEAR,"0"));
    }

    @Override
    public String getSortType() {
        return sharedPreferences.getString(KEY_SP_ST_SORT,"");
    }


    @Override
    public void clear() {
        sharedPreferences.edit().clear().apply();
    }


}
