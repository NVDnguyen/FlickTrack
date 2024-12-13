package com.example.myapplication.domain.model;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

public class Movie implements Parcelable {
    private int id;

    private String title;
    private boolean adult;
    private String overview;
    private String release_date;
    private String poster_path;
    private String original_title;
    private double vote_average;
    private boolean isFav = false;

    public boolean isFav() {
        return isFav;
    }

    public void setFav(boolean fav) {
        isFav = fav;
    }

    public Movie() {
    }

    // Constructor
    public Movie(int id, String title, boolean adult, String overview, String release_date, String poster_path,
                 String original_title, double vote_average) {
        this.id = id;
        this.title = title;
        this.adult = adult;
        this.overview = overview;
        this.release_date = release_date;
        this.poster_path = poster_path;
        this.original_title = original_title;
        this.vote_average = vote_average;
    }

    protected Movie(Parcel in) {
        id = in.readInt();
        title = in.readString();
        adult = in.readByte() != 0;
        overview = in.readString();
        release_date = in.readString();
        poster_path = in.readString();
        original_title = in.readString();
        vote_average = in.readDouble();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isAdult() {
        return adult;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public void setOriginal_title(String original_title) {
        this.original_title = original_title;
    }

    public double getVote_average() {
        return vote_average;
    }

    public void setVote_average(double vote_average) {
        this.vote_average = vote_average;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeByte((byte) (adult ? 1 : 0));
        dest.writeString(overview);
        dest.writeString(release_date);
        dest.writeString(poster_path);
        dest.writeString(original_title);
        dest.writeDouble(vote_average);
    }

    public static DiffUtil.ItemCallback<Movie> DIFF_CALLBACK = new DiffUtil.ItemCallback<Movie>() {
        @Override
        public boolean areItemsTheSame(@NonNull Movie oldItem, @NonNull Movie newItem) {
            return oldItem.id == newItem.id;
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull Movie oldItem, @NonNull Movie newItem) {
            return oldItem.equals(newItem);
        }

    };

    @Override
    public String toString() {
        return "Movie{" +
                "adult=" + adult +
                ", id=" + id +
                ", title='" + title + '\'' +
                ", overview='" + overview + '\'' +
                ", release_date='" + release_date + '\'' +
                ", poster_path='" + poster_path + '\'' +
                ", original_title='" + original_title + '\'' +
                ", vote_average=" + vote_average +
                ", isFav=" + isFav +
                '}';
    }
    public boolean isValid() {
        return title != null && !title.isEmpty() &&
                overview != null && !overview.isEmpty() &&
                release_date != null && !release_date.isEmpty() &&
                poster_path != null && !poster_path.isEmpty() &&
                original_title != null && !original_title.isEmpty();
    }

}
