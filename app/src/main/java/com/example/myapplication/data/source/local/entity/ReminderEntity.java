package com.example.myapplication.data.source.local.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "reminders")
public class ReminderEntity {
    @ColumnInfo(name = "movieId")
    @PrimaryKey(autoGenerate = false)
    private int movieId;

    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    // Movie detail
    private String title;
    private String yearRelease;
    private String rate;
    private String poster_path;
    private boolean adult;
    private String overview;
    private boolean isFav;

    // Default constructor
    public ReminderEntity() {
    }

    // Constructor
    public ReminderEntity(int day, int month, int year, int hour, int minute, int movieId
            , String title, String yearRelease, String rate, String poster_path
            , boolean adult,String overview,boolean isFav ) {
        this.day = day;
        this.month = month;
        this.year = year;
        this.hour = hour;
        this.minute = minute;
        this.movieId = movieId;
        this.title = title;
        this.yearRelease = yearRelease;
        this.rate = rate;
        this.poster_path = poster_path;
        this.adult = adult;
        this.overview = overview;
        this.isFav = isFav;
    }

    public boolean isAdult() {
        return adult;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
    }

    public boolean isFav() {
        return isFav;
    }

    public void setFav(boolean fav) {
        isFav = fav;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getYearRelease() {
        return yearRelease;
    }

    public void setYearRelease(String yearRelease) {
        this.yearRelease = yearRelease;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    @Override
    public String toString() {
        return day + "/" + month + "/" + year + " " + hour + ":" + minute ;
    }
}
