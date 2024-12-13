package com.example.myapplication.data.source.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myapplication.data.source.local.entity.MovieFavEntity;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface MovieFavDao {
    @Insert
    void insert(MovieFavEntity movie);

    @Update
    void update(MovieFavEntity movie);

    @Delete
    void delete(MovieFavEntity movie);

    @Query("SELECT * FROM favourite_movies")
    Flowable<List<MovieFavEntity>> getAllMovies();


    @Query("DELETE FROM favourite_movies")
    void deleteAllMovies();

}
