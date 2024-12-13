package com.example.myapplication.present.ui.activity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.myapplication.present.ui.fragment.about.AboutFragment;
import com.example.myapplication.present.ui.fragment.favourite.FavouriteFragment;
import com.example.myapplication.present.ui.fragment.home.controller.HomeFragment;
import com.example.myapplication.present.ui.fragment.setting.SettingsFragment;

import java.util.ArrayList;
import java.util.List;


public class ViewPagerAdapter extends FragmentStateAdapter {
    private final List<Fragment> fragmentList;
    private final HomeFragment homeFragment;
    private final FavouriteFragment favouriteFragment;

    public ViewPagerAdapter(@NonNull FragmentActivity fragment) {
        super(fragment);
        homeFragment = new HomeFragment();
        favouriteFragment = new FavouriteFragment();
        fragmentList = new ArrayList<>();
        fragmentList.add(homeFragment);
        fragmentList.add(favouriteFragment);
        fragmentList.add(new SettingsFragment());
        fragmentList.add(new AboutFragment());
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getItemCount() {
        return fragmentList.size();
    }
    public Fragment getFragment(int pos){
        return fragmentList.get(pos);
    }

}
