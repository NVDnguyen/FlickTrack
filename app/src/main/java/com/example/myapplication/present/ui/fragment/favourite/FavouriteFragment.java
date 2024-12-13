package com.example.myapplication.present.ui.fragment.favourite;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.domain.model.Movie;
import com.example.myapplication.present.viewmodel.MyViewModel;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class FavouriteFragment extends Fragment {
    private FavouriteAdapter favouriteAdapter;
    private final List<Movie> listFavMovie = new ArrayList<>();
    public FavouriteFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view =  inflater.inflate(R.layout.fragment_favourite, container, false);
        MyViewModel myViewModel = new ViewModelProvider(requireActivity()).get(MyViewModel.class);

        favouriteAdapter = new FavouriteAdapter(new ArrayList<>(), myViewModel,getViewLifecycleOwner());
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_favourite);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(favouriteAdapter);

       myViewModel.getFavMovies().observe(getViewLifecycleOwner(), new Observer<List<Movie>>() {
           @Override
           public void onChanged(List<Movie> movies) {
               listFavMovie.clear();
               listFavMovie.addAll(movies);
               favouriteAdapter.updateData(listFavMovie);
           }
       });

       return  view;
    }
    public void searchMovie(String query){
        if(query.isEmpty()){
            favouriteAdapter.updateData(listFavMovie);
        }else{
            List<Movie> listDisplay = new ArrayList<>();
            for (Movie m:
                 listFavMovie) {
                if(m.getTitle().contains(query)){
                    listDisplay.add(m);
                }

            }
            favouriteAdapter.updateData(listDisplay);
        }

    }
}
