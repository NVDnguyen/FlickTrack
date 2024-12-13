package com.example.myapplication.domain.usecase.uc;


import androidx.lifecycle.LiveData;

import com.example.myapplication.domain.model.Movie;
import com.example.myapplication.domain.repository.FavMovieRepository;
import com.example.myapplication.domain.repository.MovieRepository;
import com.example.myapplication.domain.usecase.base.FlowableUseCase;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import kotlinx.coroutines.CoroutineScope;

public class GetFavMovieUseCase {
    private final FavMovieRepository favMovieRepository;
    @Inject
    public GetFavMovieUseCase(FavMovieRepository favMovieRepository) {
        this.favMovieRepository = favMovieRepository;
    }

    public Completable addFavMovie(Movie m){
        return favMovieRepository.addFavMovie(m);
    }
    public Completable deleteFavMovie(Movie m){
        return favMovieRepository.deleteFavMovie(m);
    }

    public LiveData<List<Movie>> getFavMovies(){
        return favMovieRepository.getFavMovies();
    }
    public void clear(){
        favMovieRepository.clear();
    }
}
