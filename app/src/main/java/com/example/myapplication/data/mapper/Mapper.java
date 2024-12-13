package com.example.myapplication.data.mapper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.example.myapplication.data.source.local.entity.MovieFavEntity;
import com.example.myapplication.data.source.local.entity.ReminderEntity;
import com.example.myapplication.data.source.remote.firebase.UserEntity;
import com.example.myapplication.data.source.remote.movieAPI.entity.MovieEntity;
import com.example.myapplication.data.source.remote.movieAPI.entity.StaffEntity;
import com.example.myapplication.data.source.remote.movieAPI.entity.StaffsResponse;
import com.example.myapplication.domain.model.Movie;
import com.example.myapplication.domain.model.Reminder;
import com.example.myapplication.domain.model.Staff;
import com.example.myapplication.domain.model.User;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.inject.Inject;

public class Mapper {

    @Inject
    public Mapper() {
    }
    public <T> List<Movie> mapToDomainList(List<T> entities, Function<T, Movie> mapperFunction) {
        return entities.stream()
                .map(mapperFunction)
                .collect(Collectors.toList());
    }

    public Movie mapMvToDomain(MovieEntity movieData) {
        return new Movie(
                movieData.getId(),
                movieData.getTitle(),
                movieData.isAdult(),
                movieData.getOverview(),
                movieData.getRelease_date(),
                movieData.getPoster_path(),
                movieData.getOriginal_title(),
                movieData.getVote_average()
        );
    }

    public Movie mapFavToDomain(MovieFavEntity movieFavEntity) {
        return new Movie(
                movieFavEntity.getId(),
                movieFavEntity.getTitle(),
                movieFavEntity.isAdult(),
                movieFavEntity.getOverview(),
                movieFavEntity.getRelease_date(),
                movieFavEntity.getPoster_path(),
                movieFavEntity.getOriginal_title(),
                movieFavEntity.getVote_average()
        );
    }
    public MovieFavEntity mapDomainToFav(Movie movie) {
        Log.d("LogLog","add fav movie-"+movie.toString());
        MovieFavEntity movieFavEntity = new MovieFavEntity();
        movieFavEntity.setId(movie.getId());
        movieFavEntity.setTitle(movie.getTitle());
        movieFavEntity.setAdult(movie.isAdult());
        movieFavEntity.setOverview(movie.getOverview());
        movieFavEntity.setRelease_date(movie.getRelease_date());
        movieFavEntity.setPoster_path(movie.getPoster_path());
        movieFavEntity.setOriginal_title(movie.getOriginal_title());
        movieFavEntity.setVote_average(movie.getVote_average());
        return movieFavEntity;
    }


    public Staff mapStaffEntityToDomain(StaffEntity staffEntity){
        return new Staff(
                staffEntity.isAdult(),
                staffEntity.getGender(),
                staffEntity.getId(),
                staffEntity.getKnown_for_department(),
                staffEntity.getName(),
                staffEntity.getOriginal_name(),
                staffEntity.getPopularity(),
                staffEntity.getProfile_path(),
                staffEntity.getCredit_id(),
                staffEntity.getDepartment(),
                staffEntity.getJob()
        );
    }
    public List<Staff> mapStaffResponseToListStaff(StaffsResponse staffsResponse){
        List<Staff> casts = staffsResponse.getCast().stream().map(this::mapStaffEntityToDomain).collect(Collectors.toList());
        List<Staff> crews = staffsResponse.getCrew().stream().map(this::mapStaffEntityToDomain).collect(Collectors.toList());
        casts.addAll(crews);
        return casts;
    }

    public User mapUserEntitytoUser(UserEntity userEntity){
        if(userEntity==null) return null;
        return new User(
                userEntity.getId(),
                userEntity.getName(),
                convertStringToImageBitmap(userEntity.getImage()),
                userEntity.getEmail(),
                userEntity.getDob(),
                userEntity.getSex()
        );
    }
    public UserEntity mapUserToUserEntity(User u){
        UserEntity userEntity = new UserEntity();
        userEntity.setId(u.getId());
        userEntity.setDob(u.getDob());
        userEntity.setEmail(u.getEmail());
        userEntity.setName(u.getName());
        userEntity.setImage(convertImageBitmapToString(u.getImage()));
        userEntity.setSex(u.getSex());
        return userEntity;
    }
    public ReminderEntity mapReminderToReminderEntity(Reminder reminder) {
        if(reminder==null) return null;
        return new ReminderEntity(
                reminder.getDay(),
                reminder.getMonth(),
                reminder.getYear(),
                reminder.getHour(),
                reminder.getMinute(),
                reminder.getMovieId(),
                reminder.getTitle(),
                reminder.getYearRelease(),
                reminder.getRate(),
                reminder.getPoster_path(),
                reminder.isAdult(),
                reminder.getOverview(),
                reminder.isFav()
        );
    }

    public Reminder mapReminderEntityToReminder(ReminderEntity reminderEntity) {
        if(reminderEntity==null) return null;
        return new Reminder(
                reminderEntity.getYear(),
                reminderEntity.getMonth(),
                reminderEntity.getDay(),
                reminderEntity.getHour(),
                reminderEntity.getMinute(),
                reminderEntity.getMovieId(),
                reminderEntity.getTitle(),
                reminderEntity.getYearRelease(),
                reminderEntity.getRate(),
                reminderEntity.getPoster_path(),
                reminderEntity.isAdult(),
                reminderEntity.getOverview(),
                reminderEntity.isFav()
        );
    }


    // Convert the Bitmap to a Base64-encoded string and store it
    public static String convertImageBitmapToString(Bitmap image) {
        try {
            if (image != null) {
                // Resize the image to a smaller size
                int maxWidth = 300;  // Max width in pixels
                int maxHeight = 300; // Max height in pixels

                int width = image.getWidth();
                int height = image.getHeight();

                // Calculate the ratio to maintain the aspect ratio
                float ratio = Math.min((float) maxWidth / width, (float) maxHeight / height);
                int newWidth = Math.round(width * ratio);
                int newHeight = Math.round(height * ratio);

                // Resize the image
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(image, newWidth, newHeight, false);

                // Compress the resized image
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream); // JPEG with 80% quality
                byte[] byteArray = byteArrayOutputStream.toByteArray();

                // Convert the compressed image to Base64
                return Base64.encodeToString(byteArray, Base64.DEFAULT);
            } else {
                return "null";
            }
        } catch (Exception e) {
            Log.e("LogLog", "Error setting image", e);
            return "null";
        }
    }
    // Convert the Base64 image string back to a Bitmap
    public static Bitmap convertStringToImageBitmap(String image) {
        try{
            if (image != null && !image.isEmpty()) {
                byte[] decodedString = Base64.decode(image, Base64.DEFAULT);
                return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            }
        }catch (Exception e){
            Log.e("LogLog","getImageBitmap-> java.lang.IllegalArgumentException: bad base-64");
        }

        return null;  // Return null if image is empty or null
    }

}
