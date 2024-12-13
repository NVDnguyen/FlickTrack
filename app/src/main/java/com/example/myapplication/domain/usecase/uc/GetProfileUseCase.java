package com.example.myapplication.domain.usecase.uc;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import com.example.myapplication.domain.model.Result;
import com.example.myapplication.domain.model.User;
import com.example.myapplication.domain.repository.ProfileRepository;

import javax.inject.Inject;

public class GetProfileUseCase {
    private final ProfileRepository profileRepository;
    @Inject
    public GetProfileUseCase(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    public LiveData<Result<User>> loadUserData(LifecycleOwner lifecycleOwner){
        return profileRepository.loadUserData(lifecycleOwner);
    }
    public LiveData<Result<Boolean>> saveUserData(User user,LifecycleOwner lifecycleOwner){
        return profileRepository.saveUserData(user,lifecycleOwner);
    }
    public LiveData<Result<Boolean>> signIn(String email, String password){
        return profileRepository.signIn(email,password);
    }
    public LiveData<Result<Boolean>> clear(){
        return profileRepository.clear();
    }

}
