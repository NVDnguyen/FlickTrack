package com.example.myapplication.domain.usecase.uc;

import com.example.myapplication.domain.model.Staff;
import com.example.myapplication.domain.repository.MovieRepository;
import com.example.myapplication.domain.usecase.base.SingleUseCase;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;

public class GetStaffUseCase extends SingleUseCase<List<Staff>> {

    private final MovieRepository movieRepository;
    private int movieID;

    @Inject
    public GetStaffUseCase(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @Override
    protected Single<List<Staff>> buildUseCaseSingle() {
        return movieRepository.loadCastAndCrew(movieID);
    }
    public void setMovieID(int movieID) {
        this.movieID = movieID;
    }
}
