package com.example.myapplication.data.source.remote.movieAPI;

import com.example.myapplication.data.source.remote.movieAPI.entity.MovieEntity;
import com.example.myapplication.data.source.remote.movieAPI.entity.MoviesResponse;
import com.example.myapplication.data.source.remote.movieAPI.entity.StaffsResponse;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitAPI {
    @GET("movie/popular")
    Single<MoviesResponse> getPopularMovies(@Query("api_key") String apiKey, @Query("page") long page);



    @GET("movie/{type}")
    Single<MoviesResponse> getMovies(
            @Path("type") String type,
            @Query("page") long pageNumber
    );
    @GET("movie/{movieId}")
    Single<MovieEntity> getMovieDetails(
            @Path("movieId") int movieId
    );

    @GET("movie/{movieId}/credits")
    Single<StaffsResponse> getCastAndCrew(
            @Path("movieId") int movieId
    );


}
