package com.example.myapplication.present.ui.fragment.home.controller;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.domain.model.Movie;
import com.example.myapplication.present.viewmodel.MyViewModel;
import com.example.myapplication.R;


import dagger.hilt.android.AndroidEntryPoint;
@AndroidEntryPoint
public class HomeFragment extends Fragment {
    private final MutableLiveData<NavController> navControllerMutableLiveData = new MutableLiveData<>();


    public HomeFragment() {
    }


    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        NavHostFragment navHostFragment = (NavHostFragment) getChildFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        MyViewModel myViewModel =new ViewModelProvider(requireActivity()).get(MyViewModel.class);
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            navControllerMutableLiveData.setValue(navController);
            myViewModel.getMovieSelected().observe(getViewLifecycleOwner(), new Observer<Movie>() {
                @Override
                public void onChanged(Movie movie) {
                    if(movie!= null){
                        navController.navigate(R.id.detailMovieFragment);
                    }
                }
            });

        }


        return view;
    }
    public LiveData<NavController> getNavController(){
        return navControllerMutableLiveData;
    }


}


