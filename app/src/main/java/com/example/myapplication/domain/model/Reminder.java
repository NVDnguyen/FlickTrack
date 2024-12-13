package com.example.myapplication.domain.model;


import com.google.gson.Gson;

import java.io.Serializable;

public class Reminder implements Serializable {
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;

    private int movieId;
    private String title;
    private String yearRelease;
    private String rate;
    private String poster_path;
    private boolean adult;
    private String overview;
    private boolean isFav;

    // Default constructor
    public Reminder() {
    }

    // Constructor
    public Reminder(int day, int month, int year, int hour, int minute, int movieId
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
    // Constructor with 6 params including a Movie object
    public Reminder(int year, int month, int day, int hour, int minute, Movie movie) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;

        // Initialize fields using the Movie object
        this.movieId = movie.getId();
        this.title = movie.getTitle();
        this.yearRelease = movie.getRelease_date();
        this.rate = String.valueOf(movie.getVote_average());
        this.poster_path = movie.getPoster_path();
        this.adult = movie.isAdult();
        this.overview = movie.getOverview();
        this.isFav = movie.isFav();
    }
    public Movie getMovieModel() {
        Movie movie = new Movie();
        movie.setId(this.movieId);
        movie.setTitle(this.title);
        movie.setRelease_date(this.yearRelease);
        movie.setVote_average(Double.parseDouble(this.rate));
        movie.setPoster_path(this.poster_path);
        movie.setAdult(this.adult);
        movie.setOverview(this.overview);
        movie.setFav(this.isFav);
        return movie;
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

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
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

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
    @Override
    public String toString() {
        return
                day + "/" + month + "/" + year + " " + hour + ":" + minute ;
    }
    public static String toJson(Reminder reminder) {
        Gson gson = new Gson();
        return gson.toJson(reminder);
    }

    // Static method to convert JSON to Reminder
    public static Reminder fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, Reminder.class);
    }
}
