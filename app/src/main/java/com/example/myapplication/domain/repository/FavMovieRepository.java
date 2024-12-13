package com.example.myapplication.domain.repository;

import androidx.lifecycle.LiveData;

import com.example.myapplication.domain.model.Movie;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;

public interface FavMovieRepository {
    // Room Database
    LiveData<List<Movie>> getFavMovies() ;
    Completable addFavMovie(Movie movie);
    Completable deleteFavMovie(Movie movie);
    void clear();
}
