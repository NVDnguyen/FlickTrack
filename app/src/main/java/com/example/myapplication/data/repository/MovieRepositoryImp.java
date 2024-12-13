package com.example.myapplication.data.repository;

import androidx.paging.Pager;
import androidx.paging.PagingConfig;
import androidx.paging.PagingData;
import androidx.paging.rxjava2.PagingRx;

import com.example.myapplication.data.mapper.Mapper;
import com.example.myapplication.data.source.remote.movieAPI.RetrofitClient;
import com.example.myapplication.domain.model.Staff;
import com.example.myapplication.domain.repository.FavMovieRepository;
import com.example.myapplication.domain.repository.MovieRepository;
import com.example.myapplication.data.paging.MoviesSource;
import com.example.myapplication.domain.model.Movie;
import com.example.myapplication.domain.repository.DataSettingRepository;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;
import kotlinx.coroutines.CoroutineScope;

import javax.inject.Inject;

public class MovieRepositoryImp implements MovieRepository {

    private  MoviesSource moviesSource;
    private final FavMovieRepository favMovieRepository;
    private final Mapper mapper;
    private final DataSettingRepository dataSettingRepository;

    @Inject
    public MovieRepositoryImp(FavMovieRepository favMovieRepository, DataSettingRepository dataSettingRepository, Mapper mapper) {
        this.favMovieRepository = favMovieRepository;
        this.dataSettingRepository = dataSettingRepository;
        this.mapper = mapper;
        this.moviesSource = new MoviesSource(mapper,favMovieRepository, dataSettingRepository);
    }

    @Override
    public void refreshMovieSource() {
        this.moviesSource = new MoviesSource(mapper,favMovieRepository, dataSettingRepository);
    }

    @Override
    public Flowable<PagingData<Movie>> getMovies(CoroutineScope viewmodelScope) {
        Pager<Integer, Movie> pager = new Pager<>(
                new PagingConfig(10, 2,false,10,20),
                () -> moviesSource
        );
        return PagingRx.cachedIn( PagingRx.getFlowable(pager),viewmodelScope);
    }

    @Override
    public Single<Movie> loadMovieDetail(int movieId) {
        return RetrofitClient.create().getMovieDetails(movieId).map(mapper::mapMvToDomain);
    }

    @Override
    public Single<List<Staff>> loadCastAndCrew(int movieId) {
        return RetrofitClient.create().getCastAndCrew(movieId).map(mapper::mapStaffResponseToListStaff);
    }




}
