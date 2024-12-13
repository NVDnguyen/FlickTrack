package com.example.myapplication.domain.repository;

public interface DataSettingRepository {

    String getCategory();
    int getFilterByRating();
    int getFilterByYear();
    String getSortType();

     void clear();

}
