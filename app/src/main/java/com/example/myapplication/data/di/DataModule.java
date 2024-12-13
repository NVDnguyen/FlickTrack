package com.example.myapplication.data.di;

import android.content.Context;

import androidx.room.Room;

import com.example.myapplication.data.mapper.Mapper;
import com.example.myapplication.data.paging.MoviesSource;
import com.example.myapplication.data.repository.FavMovieRepositoryImp;
import com.example.myapplication.data.repository.MovieRepositoryImp;
import com.example.myapplication.data.repository.DataSettingRepositoryImp;
import com.example.myapplication.data.repository.ProfileRepositoryImp;
import com.example.myapplication.data.repository.ReminderRepositoryImp;
import com.example.myapplication.data.source.local.AppDatabase;
import com.example.myapplication.data.source.local.dao.MovieFavDao;
import com.example.myapplication.data.source.local.dao.ReminderDao;
import com.example.myapplication.domain.repository.FavMovieRepository;
import com.example.myapplication.domain.repository.MovieRepository;
import com.example.myapplication.domain.repository.DataSettingRepository;
import com.example.myapplication.domain.usecase.uc.GetFavMovieUseCase;
import com.example.myapplication.domain.usecase.uc.GetMovieUseCase;
import com.example.myapplication.domain.usecase.uc.GetProfileUseCase;
import com.example.myapplication.domain.usecase.uc.GetReminderUseCase;
import com.example.myapplication.domain.usecase.uc.GetStaffUseCase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class DataModule {

    /*
    * INDEPENDENCE
    * */

    @Provides
    @Singleton
    public AppDatabase provideDatabase(@ApplicationContext Context context) {
        return Room.databaseBuilder(context, AppDatabase.class, "app_database")
                .fallbackToDestructiveMigration()
                .build();
    }
    @Provides
    public MovieFavDao provideMovieFavDao(AppDatabase database) {
        return database.movieFavDao();
    }
    @Provides
    public ReminderDao provideReminderDao(AppDatabase database){
        return database.reminderDao();
    }

    @Provides
    @Singleton
    public Mapper provideMovieMapper() {
        return new Mapper();
    }

    @Provides
    @Singleton
    public DataSettingRepository providePreferenceRepository(@ApplicationContext Context context){
        return new DataSettingRepositoryImp(context);
    }


    /*
    * DEPENDENCE
    * */
    @Provides
    @Singleton
    public ReminderRepositoryImp provideReminderRepositoryImp(Mapper mapper, ReminderDao reminderDao){
        return new ReminderRepositoryImp(mapper,reminderDao);
    }

    @Provides
    @Singleton
    public ProfileRepositoryImp profileRepositoryImp(Mapper mapper, @ApplicationContext Context context){
        return new ProfileRepositoryImp(mapper, context);
    }


    @Provides
    @Singleton
    public FavMovieRepository favMovieRepository(Mapper mapper, MovieFavDao movieFavDao){
        return new FavMovieRepositoryImp(mapper, movieFavDao);
    }



    @Provides
    @Singleton
    public MovieRepository provideMovieRepository(DataSettingRepository dataSettingRepository, FavMovieRepository favMovieRepository, Mapper mapper) {
        return new MovieRepositoryImp(favMovieRepository,dataSettingRepository, mapper);
    }


    @Provides
    @Singleton
    public MoviesSource provideMoviesSource(Mapper mapper, FavMovieRepository favMovieRepository, DataSettingRepository provideDataSettingRepository) {
        return new MoviesSource(mapper,favMovieRepository, provideDataSettingRepository);
    }


    /*
    * USE CASE
    * */
    @Provides
    @Singleton
    public GetMovieUseCase getMovieUseCase(MovieRepository movieRepository){
        return new GetMovieUseCase(movieRepository);
    }

    @Provides
    @Singleton
    public GetFavMovieUseCase getFavMovieUseCase(FavMovieRepository movieRepository){
        return new GetFavMovieUseCase(movieRepository);
    }
    @Provides
    @Singleton
    public GetStaffUseCase getStaffUseCase(MovieRepository movieRepository){
        return  new GetStaffUseCase(movieRepository);
    }

    @Provides
    @Singleton
    public GetProfileUseCase getPreferenceUseCase(ProfileRepositoryImp profileRepositoryImp){
        return new GetProfileUseCase(profileRepositoryImp);
    }

    @Provides
    @Singleton
    public GetReminderUseCase getReminderUseCase(ReminderRepositoryImp reminderRepositoryImp){
        return new GetReminderUseCase(reminderRepositoryImp);
    }


}
