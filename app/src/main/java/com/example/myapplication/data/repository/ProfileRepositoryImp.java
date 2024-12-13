package com.example.myapplication.data.repository;

import static com.example.myapplication.app.Constants.KEY_SP_USER;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.preference.PreferenceManager;

import com.example.myapplication.data.mapper.Mapper;
import com.example.myapplication.data.source.remote.firebase.FireBaseHelper;
import com.example.myapplication.data.source.remote.firebase.UserEntity;
import com.example.myapplication.domain.model.Result;
import com.example.myapplication.domain.model.User;
import com.example.myapplication.domain.repository.ProfileRepository;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Inject;

public class ProfileRepositoryImp implements ProfileRepository {
    private final FireBaseHelper fireBaseHelper = new FireBaseHelper();
    private final Mapper mapper;
    private final SharedPreferences sharedPreferences;
    private final Executor executor;
    private final Context context;
    private final MutableLiveData<Boolean> isConnected = new MutableLiveData<>();

    @Inject
    public ProfileRepositoryImp(Mapper mapper, Context context ) {
        this.mapper = mapper;
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.context = context;
        this.executor = Executors.newSingleThreadExecutor();
        listenConnectState();
    }

    @Override
    public LiveData<Result<User>> loadUserData(LifecycleOwner lifecycleOwner) {
        MutableLiveData<Result<User>> result = new MutableLiveData<>();

        // 1. Load user data from local storage
        executor.execute(() -> {
            String userString = sharedPreferences.getString(KEY_SP_USER, null);
            if (userString != null) {
                User localUser = mapper.mapUserEntitytoUser(UserEntity.convertStringToUserEntity(userString));
                result.postValue(Result.success(localUser));
            } else {
                result.postValue(Result.error("No local user data found."));
            }
        });

        // 2. Observe network status and sync with Firebase
        isConnected.observe(lifecycleOwner, connected -> {
            if (Boolean.TRUE.equals(connected)) {
                fireBaseHelper.fetchUserFromDatabase().observe(lifecycleOwner, userEntity -> {
                    if (userEntity != null) {
                        User cloudUser = mapper.mapUserEntitytoUser(userEntity);

                        // Sync with local storage if different
                        String localUserString = sharedPreferences.getString(KEY_SP_USER, "");
                        User localUser = mapper.mapUserEntitytoUser(UserEntity.convertStringToUserEntity(localUserString));

                        if (!cloudUser.equals(localUser)) {
                            sharedPreferences.edit().putString(
                                    KEY_SP_USER, UserEntity.convertUserEntityToString(userEntity)
                            ).apply();
                        }

                        result.setValue(Result.success(cloudUser));
                    } else {
                        result.setValue(Result.success(new User()));
                        Log.d("loglog","User data not found in Firebase.");
                    }
                });
            } else {
                result.setValue(Result.error("No internet connection."));
            }
        });

        return result;
    }

    @Override
    public LiveData<Result<Boolean>> saveUserData(User user, LifecycleOwner lifecycleOwner) {
        MutableLiveData<Result<Boolean>> result = new MutableLiveData<>();

        // Save to local storage
//        executor.execute(() -> {
//            sharedPreferences.edit().putString(
//                    KEY_SP_USER, UserEntity.convertUserEntityToString(mapper.mapUserToUserEntity(user))
//            ).apply();
//            result.postValue(Result.success(true));
//        });

        // Sync with Firebase if connected
        Log.d("firebaseLog"," Sync with Firebase if connected");

        isConnected.observe(lifecycleOwner, connected -> {
            Log.d("firebaseLog"," Connection -> "+connected.toString());

            if (Boolean.TRUE.equals(connected)) {
                fireBaseHelper.saveUserToDatabase(mapper.mapUserToUserEntity(user))
                        .observe(lifecycleOwner, success -> {
                            if (Boolean.TRUE.equals(success)) {
                                result.setValue(Result.success(true));
                            } else {
                                result.setValue(Result.error("Failed to save data to Firebase."));
                            }
                        });
            }else{
                result.setValue(Result.error("No internet connection."));
                Log.d("firebaseLog","No internet connection");

            }
        });

        return result;
    }

    @Override
    public LiveData<Result<Boolean>> signIn(String email, String password) {
        MutableLiveData<Result<Boolean>> result = new MutableLiveData<>();
        isConnected.observeForever(connected -> {
            if (Boolean.FALSE.equals(connected)) {
                result.setValue(Result.error("No internet connection. Please try again later."));
            } else {
                // Perform sign-in via Firebase
                fireBaseHelper.signIn(email, password).observeForever(success -> {
                    if (Boolean.TRUE.equals(success)) {
                        result.setValue(Result.success(true));
                    } else {
                        result.setValue(Result.error("Sign-in failed. Please check your credentials."));
                    }
                });
            }
        });

        return result;
    }

    @Override
    public LiveData<Result<Boolean>> clear() {
        MutableLiveData<Result<Boolean>> result = new MutableLiveData<>();
        executor.execute(() -> {
            sharedPreferences.edit().remove(KEY_SP_USER).apply();
            result.postValue(Result.success(true));
        });
        return result;
    }

    private void listenConnectState() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            // Check the network status immediately
            Network activeNetwork = connectivityManager.getActiveNetwork();
            if (activeNetwork != null) {
                isConnected.setValue(true); // Network is available
            } else {
                isConnected.setValue(false); // No network available
            }

            // Register a callback to listen for network changes
            connectivityManager.registerDefaultNetworkCallback(new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(@NonNull Network network) {
                    isConnected.postValue(true); // Network is connected
                }

                @Override
                public void onLost(@NonNull Network network) {
                    isConnected.postValue(false); // Network is lost
                }
            });
        } else {
            isConnected.setValue(false); // Unable to get ConnectivityManager
        }
    }

}
