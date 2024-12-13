package com.example.myapplication.present.viewmodel;

import android.annotation.SuppressLint;
import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelKt;
import androidx.paging.PagingData;

import com.example.myapplication.domain.model.Movie;
import com.example.myapplication.domain.model.Staff;
import com.example.myapplication.domain.usecase.uc.GetFavMovieUseCase;
import com.example.myapplication.domain.usecase.uc.GetMovieUseCase;
import com.example.myapplication.domain.usecase.uc.GetProfileUseCase;
import com.example.myapplication.domain.usecase.uc.GetReminderUseCase;
import com.example.myapplication.domain.usecase.uc.GetStaffUseCase;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


@HiltViewModel

public class MyViewModel extends AndroidViewModel {
    private final String TAG ="MyViewModelLOG";
    private final GetMovieUseCase getMovieUseCase;
    private final GetFavMovieUseCase getFavMovieUseCase;
    private final GetStaffUseCase getStaffUseCase;
    private final GetProfileUseCase profileUseCase;
    private final GetReminderUseCase reminderUseCase;

    // data
    private final MutableLiveData<PagingData<Movie>> pagingMovies = new MutableLiveData<>();

    private final MutableLiveData<Movie> movieSelected = new MutableLiveData<>(null);

    private final MutableLiveData<Movie> movieChanged = new MutableLiveData<>(null);


    // state for ui
    private final MutableLiveData<Integer> typeLayoutListMovie = new MutableLiveData<>();
    private final MutableLiveData<Boolean> settingHasChanged = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> toolBarForHome = new MutableLiveData<>(true);
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Inject
    public MyViewModel(@NonNull Application application,
                       GetMovieUseCase getMovieUseCase,
                       GetFavMovieUseCase getFavMovieUseCase,
                       GetStaffUseCase getStaffUseCase,
                       GetProfileUseCase profileUseCase,
                       GetReminderUseCase reminderUseCase
    ) {
        super(application);
        this.getMovieUseCase = getMovieUseCase;
        this.getFavMovieUseCase = getFavMovieUseCase;
        this.getStaffUseCase = getStaffUseCase;
        this.profileUseCase = profileUseCase;
        this.reminderUseCase = reminderUseCase;
        fetchMovies();
    }



    public void fetchMovies() {
        getMovieUseCase.execute(
                pagingMovies::setValue,
                throwable -> {
                    Log.e(TAG, "getMovieUseCase : "+throwable.toString());
                },
                ()->Log.e(TAG, "getMovieUseCase done"),
                ViewModelKt.getViewModelScope(this)
        );
    }



    public MutableLiveData<PagingData<Movie>> getPagingMovies() {
        return pagingMovies;

    }

    public MutableLiveData<Integer> getTypeLayoutListMovie() {
        return typeLayoutListMovie;
    }
    public void setTypeLayoutListMovie(int type){
        typeLayoutListMovie.setValue(type);
    }

    public LiveData<List<Staff>> loadCastAndCrew(int movieID) {
        MutableLiveData<List<Staff>> liveData = new MutableLiveData<>();
        getStaffUseCase.setMovieID(movieID);
        getStaffUseCase.execute(result -> {
                    liveData.postValue(result);
                    Log.d("LogLog", "success > " + result.size());
                },
                throwable -> Log.e("LogLog", "loadCastAndCrew ", throwable),
                () -> Log.d("LogLog", "loadCastAndCrew finish"));
        return liveData;
    }

    public LiveData<Movie> getMovieDetail(Movie m){
        MutableLiveData<Movie> movie = new MutableLiveData<>(new Movie());
        Disposable disposable = getMovieUseCase.getMovieDetail(m.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                movie::setValue,
                throwable -> {
                    Log.e(TAG, "myViewModel.getMovieUseCase().getMovieDetail"+m.getId() +"__" + throwable.toString());
                }
        );
        compositeDisposable.add(disposable);
        return  movie;
    }
    // movie selected
    public MutableLiveData<Movie> getMovieSelected() {
        return movieSelected;
    }
    public void setMovieSelected(Movie m){
        movieSelected.setValue(m);
    }

    // fav movie
    public LiveData<List<Movie>> getFavMovies() {
        return getFavMovieUseCase.getFavMovies();
    }
    public MutableLiveData<Movie> getMovieChanged() {
        return movieChanged;
    }
    public void setMovieChanged(Movie movie){
        movieChanged.setValue(movie);
    }

    @SuppressLint("CheckResult")
    public void addFavMovie(Movie m) {
        setMovieChanged(m);
        compositeDisposable.add(getFavMovieUseCase.addFavMovie(m).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> {
                            Log.d(TAG, "Movie added successfully");
                            Toast.makeText(getApplication(), "Movie added successfully", Toast.LENGTH_SHORT).show();
                        },
                        throwable -> {
                            Toast.makeText(getApplication(), "Error adding movie", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Error adding movie", throwable);
                        }
                ));
    }



    @SuppressLint("CheckResult")
    public void deleteFavMovie(Movie m){
        setMovieChanged(m);
        compositeDisposable.add(getFavMovieUseCase.deleteFavMovie(m).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> {
                            Toast.makeText(getApplication(),"Movie deleted successfully",Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Movie deleted successfully");

                        },
                        throwable -> {
                            Toast.makeText(getApplication(),"Error deleting movie",Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Error deleting movie", throwable);
                        }
                )
        );
    }


    // state on change setting
    public MutableLiveData<Boolean> getSettingHasChanged() {
        return settingHasChanged;
    }
    public void setSettingHasChanged(Boolean state){
        settingHasChanged.setValue(state);
    }
    public void refreshPagingMovies(){
        pagingMovies.setValue(PagingData.empty());
        getMovieUseCase.refreshPagingSource();
        fetchMovies();
    }

    public MutableLiveData<Boolean> getToolBarForHome() {
        return toolBarForHome;
    }

    public void setToolBarForHome(Boolean b) {
        this.toolBarForHome.setValue(b);
    }
    // get use case


    public GetProfileUseCase getProfileUseCase() {
        return profileUseCase;
    }
    public GetReminderUseCase getReminderUseCase(){
        return reminderUseCase;
    }
    public GetMovieUseCase getMovieUseCase(){return getMovieUseCase;}



    @Override
    protected void onCleared() {
        super.onCleared();
        getFavMovieUseCase.clear();
        compositeDisposable.clear();
    }
}
