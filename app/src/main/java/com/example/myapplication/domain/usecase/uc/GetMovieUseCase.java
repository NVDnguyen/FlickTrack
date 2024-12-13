package com.example.myapplication.domain.usecase.uc;

import androidx.paging.PagingData;

import com.example.myapplication.domain.model.Movie;
import com.example.myapplication.domain.repository.MovieRepository;
import com.example.myapplication.domain.usecase.base.FlowableUseCase;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.Single;
import kotlinx.coroutines.CoroutineScope;

public class GetMovieUseCase extends FlowableUseCase<PagingData<Movie>> {


    private final MovieRepository movieRepository;

    @Inject
    public GetMovieUseCase(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @Override
    protected Flowable<PagingData<Movie>> buildUseCaseFlowable(CoroutineScope viewmodelScope) {
        return movieRepository.getMovies(viewmodelScope);
    }
    public void refreshPagingSource(){
        movieRepository.refreshMovieSource();
    }
    public Single<Movie> getMovieDetail(int movieId){
        return movieRepository.loadMovieDetail(movieId);
    }


}
