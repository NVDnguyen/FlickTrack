package com.example.myapplication.data.repository;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.myapplication.data.mapper.Mapper;
import com.example.myapplication.data.source.local.dao.MovieFavDao;
import com.example.myapplication.domain.model.Movie;
import com.example.myapplication.domain.repository.FavMovieRepository;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class FavMovieRepositoryImp implements FavMovieRepository {
    private final Mapper mapper;
    private final MovieFavDao movieFavDao;
    private final MutableLiveData<List<Movie>> favMoviesCacheData = new MutableLiveData<>();
    private Disposable favMoviesDisposable;


    public FavMovieRepositoryImp(Mapper mapper, MovieFavDao movieFavDao) {
        this.mapper = mapper;
        this.movieFavDao = movieFavDao;
        loadFavMoviesFromDb();
    }
    //Room database
    @SuppressLint("CheckResult")
    private void loadFavMoviesFromDb() {
        favMoviesDisposable = movieFavDao.getAllMovies()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(favEntities -> {
                    favMoviesCacheData.postValue(mapper.mapToDomainList(favEntities, mapper::mapFavToDomain));
                }, throwable -> Log.e("LogLog", "Error loading favorite movies", throwable));
    }

    @Override
    public LiveData<List<Movie>> getFavMovies() {
        return favMoviesCacheData;
    }


    @Override
    public Completable addFavMovie(Movie movie) {
        return Completable.fromAction(() -> {
                    movieFavDao.insert(mapper.mapDomainToFav(movie));
                })
                .doOnComplete(this::loadFavMoviesFromDb)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .doOnError(throwable -> Log.e("LogLog", "addFavMovie", throwable));
    }



    @Override
    public Completable deleteFavMovie(Movie movie) {
        return Completable.fromAction(()->movieFavDao.delete(mapper.mapDomainToFav(movie)))
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .doOnComplete(this::loadFavMoviesFromDb)
                .doOnError(throwable -> Log.e("LogLog", "deleteFavMovie", throwable));


    }
    public void clear() {
        if (favMoviesDisposable != null && !favMoviesDisposable.isDisposed()) {
            favMoviesDisposable.dispose();
        }
    }
}
