package com.example.myapplication.data.paging;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.PagingState;
import androidx.paging.rxjava2.RxPagingSource;

import com.example.myapplication.data.mapper.Mapper;
import com.example.myapplication.domain.model.Movie;
import com.example.myapplication.data.source.remote.movieAPI.RetrofitClient;
import com.example.myapplication.domain.repository.DataSettingRepository;
import com.example.myapplication.domain.repository.FavMovieRepository;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class MoviesSource extends RxPagingSource<Integer, Movie> {

    private final Mapper mapper;
    private final FavMovieRepository favMovieRepository;
    private final DataSettingRepository dataSettingRepository;
    private int totalPages=0;
    @Inject
    public MoviesSource(Mapper mapper, FavMovieRepository favMovieRepository, DataSettingRepository dataSettingRepository) {
        this.mapper = mapper;
        this.favMovieRepository = favMovieRepository;
        this.dataSettingRepository = dataSettingRepository;
    }

    @NonNull
    @Override
    public Single<LoadResult<Integer, Movie>> loadSingle(@NonNull LoadParams<Integer> loadParams) {
        int page = loadParams.getKey() != null ? loadParams.getKey() : 1;
        // request API
        Single<List<Movie>> moviesSingle = RetrofitClient.create()
                .getMovies(dataSettingRepository.getCategory(), page)
                .subscribeOn(Schedulers.io())
                .map(response -> {
                    totalPages = response.getTotal_pages();
                    return mapper.mapToDomainList(response.getResults(), mapper::mapMvToDomain);
                });
        // Room Database
        Single<List<Movie>> favMoviesSingle = Single.create(emitter -> {
            favMovieRepository.getFavMovies().observeForever(movies -> {
                if (movies != null) {
                    emitter.onSuccess(movies);
                } else {
                    emitter.onError(new Throwable("Failed to load favorite movies"));
                }
            });
        });

        return Single.zip(moviesSingle, favMoviesSingle, (movies, favMovies) -> {
            // Sync favorite status
            syncFavorites(movies, favMovies);
            // Apply preference filter
            try {
                applyPreferenceFilter(movies);
            } catch (Exception e) {
                Log.e("fatal", "applyPreferenceFilter>" + e.toString());
            }

            return toLoadResult(movies, page, totalPages);
        }).onErrorReturn(throwable -> {
            Log.e("MoviesSource", throwable.toString());
            return new LoadResult.Error<>(throwable);
        });
    }


    private void syncFavorites(List<Movie> movies, List<Movie> favMovies) {
        for (Movie movie : movies) {
            for (Movie fav : favMovies) {
                if (movie.getId() == fav.getId()) {
                    movie.setFav(true);
                    break;
                }
            }
        }
    }

    private void applyPreferenceFilter(List<Movie> movies) {
        double rate = (double) dataSettingRepository.getFilterByRating();
        int year =  dataSettingRepository.getFilterByYear();
        List<Movie> filteredMovies = movies.stream()
                .filter(movie -> movie.getVote_average() >= rate)
                .filter(movie -> {
                    try {
                        return parseDate(movie.getRelease_date()).getYear() >= year;
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());

        if (dataSettingRepository.getSortType().equals("release_date")) {
            filteredMovies.sort(Comparator.comparing(movie -> {
                try {
                    Movie m = (Movie) movie;
                    return parseDate(m.getRelease_date());
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }).reversed());
        } else if (dataSettingRepository.getSortType().equals("rating")) {
            filteredMovies.sort(Comparator.comparing(Movie::getVote_average).reversed());
        }

        movies.clear();
        movies.addAll(filteredMovies);
    }

    private LocalDate parseDate(String str) throws ParseException {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return LocalDate.parse(str, formatter);
        } catch (Exception e) {
            throw new ParseException("Invalid date format: " + str, 0);
        }
    }

    private LoadResult<Integer, Movie> toLoadResult(List<Movie> results, Integer page, int totalPages) {
        return new LoadResult.Page<>(
                results,
                page == 1 ? null : page - 1,
                page < totalPages ? page + 1 : null
        );
    }

    @Nullable
    @Override
    public Integer getRefreshKey(@NonNull PagingState<Integer, Movie> pagingState) {
        Integer anchorPos = pagingState.getAnchorPosition();
        if (anchorPos != null) {
            return (anchorPos / 20) + 1;
        }
        return null;
    }

}
