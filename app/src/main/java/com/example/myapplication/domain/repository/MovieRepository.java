package com.example.myapplication.domain.repository;

import androidx.paging.PagingData;

import com.example.myapplication.domain.model.Movie;
import com.example.myapplication.domain.model.Staff;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import kotlinx.coroutines.CoroutineScope;

public interface MovieRepository {
    void refreshMovieSource();
    // Api
    Flowable<PagingData<Movie>> getMovies(CoroutineScope viewmodelScope);
    Single<Movie> loadMovieDetail(int movieId);
    Single<List<Staff>> loadCastAndCrew(int movieId);

}
