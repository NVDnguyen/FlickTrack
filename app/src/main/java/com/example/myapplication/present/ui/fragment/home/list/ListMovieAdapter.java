package com.example.myapplication.present.ui.fragment.home.list;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.paging.PagingDataAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.databinding.ItemMoviesBinding;
import com.example.myapplication.databinding.ItemMoviesDetailBinding;
import com.example.myapplication.databinding.NetworkItemBinding;
import com.example.myapplication.domain.model.Movie;
import com.example.myapplication.present.utils.NetworkState;

import com.example.myapplication.present.viewmodel.MyViewModel;

public class ListMovieAdapter extends PagingDataAdapter<Movie, RecyclerView.ViewHolder> {

    private final Context context;
    private int type;
    private NetworkState mNetworkState;
    private MyViewModel myViewModel;
    private final LifecycleOwner lifecycleOwner;

    private static final int TYPE_LIST = 0;
    private static final int TYPE_GRID = 1;
    private static final int TYPE_PROGRESS = 2;

    public ListMovieAdapter(Context context, int type, LifecycleOwner lifecycleOwner) {
        super(Movie.DIFF_CALLBACK);
        this.context = context;
        this.type = type;
        this.lifecycleOwner = lifecycleOwner;
    }
    public void setMoviesViewModel(MyViewModel m){
        this.myViewModel = m;

    }

    @Override
    public int getItemViewType(int position) {
        if (hasExtraRow() && position == getItemCount() - 1) {
            return TYPE_PROGRESS;
        }
        return type;
    }

    public void setViewType(int type) {
        this.type = type;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        if (viewType == TYPE_GRID) {
            ItemMoviesBinding binding = ItemMoviesBinding.inflate(layoutInflater, parent, false);
            return new MovieGridViewHolder(binding, myViewModel);
        } else if (viewType == TYPE_LIST) {
            ItemMoviesDetailBinding binding = ItemMoviesDetailBinding.inflate(layoutInflater, parent, false);
            return new MovieListViewHolder(binding, myViewModel);
        } else {
            NetworkItemBinding binding = NetworkItemBinding.inflate(layoutInflater, parent, false);
            return new NetworkStateItemViewHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
         if (holder instanceof MovieListViewHolder) {
            Movie movie = getItem(position);
            if (movie != null) {
                ((MovieListViewHolder) holder).bind(movie,lifecycleOwner);
            }
        }else if (holder instanceof MovieGridViewHolder) {
             Movie movie = getItem(position);
             if (movie != null) {
                 ((MovieGridViewHolder) holder).bind(movie);
             }
         }
         else {
            ((NetworkStateItemViewHolder) holder).bindView();
        }
    }

    private boolean hasExtraRow() {
        return mNetworkState != null && mNetworkState.getStatus() != NetworkState.Status.LOADED;
    }

    public void setNetworkState(NetworkState newNetworkState) {
        NetworkState previousState = this.mNetworkState;
        boolean previousExtraRow = hasExtraRow();
        this.mNetworkState = newNetworkState;
        boolean newExtraRow = hasExtraRow();

        if (previousExtraRow != newExtraRow) {
            if (previousExtraRow) {
                notifyItemRemoved(getItemCount());
            } else {
                notifyItemInserted(getItemCount());
            }
        } else if (newExtraRow && previousState != newNetworkState) {
            notifyItemChanged(getItemCount() - 1);
        }
    }

    // ViewHolder for Grid items
    public static class MovieGridViewHolder extends RecyclerView.ViewHolder {
        private final ItemMoviesBinding binding;
        private final MyViewModel myViewModel;

        public MovieGridViewHolder(ItemMoviesBinding binding, MyViewModel m) {
            super(binding.getRoot());
            this.binding = binding;
            this.myViewModel = m;
        }

        public void bind(Movie movie) {
            binding.setMovie(movie);
            binding.itemLayout.setOnClickListener(v->{
                myViewModel.setMovieSelected(movie);
            });
        }
    }

    // ViewHolder for List items
    public static class MovieListViewHolder extends RecyclerView.ViewHolder {
        private final ItemMoviesDetailBinding binding;
        private final MyViewModel myViewModel;
        public MovieListViewHolder(ItemMoviesDetailBinding binding, MyViewModel m) {
            super(binding.getRoot());
            this.myViewModel = m;
            this.binding = binding;

        }

        @SuppressLint("SetTextI18n")
        public void bind(Movie movie, LifecycleOwner lifecycleOwner) {
            binding.setMovie(movie);

            myViewModel.getMovieChanged().observe(lifecycleOwner, new Observer<Movie>() {
                @Override
                public void onChanged(Movie m) {
                    if(m!=null && movie.getId()==m.getId()){
                        movie.setFav(m.isFav());
                        binding.favoriteButton.setImageResource(movie.isFav() ? R.drawable.star_filled: R.drawable.star );
                        myViewModel.setMovieChanged(null);
                    }
                }
            });

            binding.itemLayout.setOnClickListener(v->{
                // show detail
                myViewModel.setMovieSelected(movie);

            });
            binding.favoriteButton.setOnClickListener(v->{
                // add to favourite list
                if(movie.isFav()){
                    myViewModel.deleteFavMovie(movie);
                }else{
                    myViewModel.addFavMovie(movie);
                }
                movie.setFav(!movie.isFav());
                binding.favoriteButton.setImageResource(movie.isFav() ? R.drawable.star_filled: R.drawable.star );

            });
        }
    }

    // ViewHolder for loading state (Progress)
    public static class NetworkStateItemViewHolder extends RecyclerView.ViewHolder {
        private final NetworkItemBinding binding;

        public NetworkStateItemViewHolder(NetworkItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindView() {
            binding.progressBar.setVisibility(View.VISIBLE);
        }
    }



}
