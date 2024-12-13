package com.example.myapplication.domain.repository;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import com.example.myapplication.domain.model.Result;
import com.example.myapplication.domain.model.User;

public interface ProfileRepository {
    LiveData<Result<User>> loadUserData(LifecycleOwner lifecycleOwner);
    LiveData<Result<Boolean>> saveUserData(User user,LifecycleOwner lifecycleOwner);
    LiveData<Result<Boolean>> signIn(String email, String password);
    LiveData<Result<Boolean>> clear();
}
