package com.example.myapplication.data.source.remote.movieAPI.entity;

import java.util.List;

public class StaffsResponse {
    private int id;
    private List<StaffEntity> cast;
    private List<StaffEntity> crew;

    public StaffsResponse() {
    }

    public StaffsResponse(int id, List<StaffEntity> cast, List<StaffEntity> crew) {
        this.id = id;
        this.cast = cast;
        this.crew = crew;
    }

    public List<StaffEntity> getCast() {
        return cast;
    }

    public void setCast(List<StaffEntity> cast) {
        this.cast = cast;
    }

    public List<StaffEntity> getCrew() {
        return crew;
    }

    public void setCrew(List<StaffEntity> crew) {
        this.crew = crew;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "StaffsResponse{" +
                "cast=" + cast +
                ", id=" + id +
                ", crew=" + crew +
                '}';
    }
}
