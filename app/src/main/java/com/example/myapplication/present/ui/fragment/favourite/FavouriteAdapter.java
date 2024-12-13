package com.example.myapplication.present.ui.fragment.favourite;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.databinding.ItemMoviesDetailBinding;
import com.example.myapplication.domain.model.Movie;
import com.example.myapplication.present.viewmodel.MyViewModel;

import java.util.List;

public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.FavouriteViewHolder> {

    private final List<Movie> favouriteItemList;
    private final MyViewModel myViewModel;
    private final LifecycleOwner lifecycleOwner;

    // Constructor
    public FavouriteAdapter(List<Movie> favouriteItemList, MyViewModel m, LifecycleOwner lifecycleOwner) {
        this.favouriteItemList = favouriteItemList;
        this.myViewModel = m;
        this.lifecycleOwner = lifecycleOwner;
    }
    @SuppressLint("NotifyDataSetChanged")
    public void updateData(List<Movie> m){
        this.favouriteItemList.clear();
        this.favouriteItemList.addAll(m);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FavouriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMoviesDetailBinding binding = ItemMoviesDetailBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new FavouriteViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FavouriteViewHolder holder, int position) {
        Movie item = favouriteItemList.get(position);
        holder.bind(item, myViewModel,lifecycleOwner);
    }

    @Override
    public int getItemCount() {
        return favouriteItemList.size();
    }

    public static class FavouriteViewHolder extends RecyclerView.ViewHolder {

        private final ItemMoviesDetailBinding binding;

        public FavouriteViewHolder(ItemMoviesDetailBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        @SuppressLint("SetTextI18n")
        public void bind(Movie movie, MyViewModel myViewModel,LifecycleOwner lifecycleOwner) {
            movie.setFav(true);
            binding.setMovie(movie);
            binding.itemLayout.setOnClickListener(v->{
                // show detail
                myViewModel.setMovieSelected(movie);

            });
            binding.favoriteButton.setOnClickListener(v->{
                // add to favourite list
                movie.setFav(false);
                myViewModel.deleteFavMovie(movie);

            });
        }
    }
}
