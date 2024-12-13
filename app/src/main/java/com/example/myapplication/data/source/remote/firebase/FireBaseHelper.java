package com.example.myapplication.data.source.remote.firebase;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.preference.PreferenceManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Objects;

public class FireBaseHelper {
    private final DatabaseReference databaseReference;
    private final FirebaseAuth auth;
    private final String TAG = "FireBaseHelper";
    public FireBaseHelper() {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        auth = FirebaseAuth.getInstance();
    }
    // Auth
    public LiveData<Boolean> signIn(String email, String password) {
        MutableLiveData<Boolean> signInStatus = new MutableLiveData<>(false);
        try{
            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User signed in");
                        } else {
                            String error = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                            Log.e(TAG, "Error signing in: " + error);
                        }
                        signInStatus.setValue(true);

                    });
        }catch (Exception e){
            signInStatus.setValue(true);

        }

        return signInStatus;
    }

    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    public void signOut() {
        auth.signOut();
        Log.d(TAG, "User signed out");
    }
    // Firebase Realtime
    public LiveData<Boolean> saveUserToDatabase(UserEntity userEntity) {
        Log.d("LogLog",userEntity.toString());
        MutableLiveData<Boolean> state = new MutableLiveData<>();
        String userId = getCurrentUser().getUid();
        userEntity.setId(userId);
        try {
            databaseReference.child(userId).setValue(userEntity)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d("LogLog", "User saved successfully");
                            state.setValue(true);
                        } else {
                            Log.e("LogLog", "Error saving user: " + task.getException());
                            state.setValue(false);
                        }
                    });
        } catch (Exception e) {
            Log.e("LogLog", "Exception during setValue: " + e.getMessage());
        }


        return state;
    }

    public LiveData<UserEntity> fetchUserFromDatabase() {
        MutableLiveData<UserEntity> userEntity = new MutableLiveData<>();
        String userId = getCurrentUser().getUid();

        // Listen for changes using a ValueEventListener
        databaseReference.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    UserEntity user = dataSnapshot.getValue(UserEntity.class);
                    if (user != null) {
                        // Update LiveData with the new value from the database
                        userEntity.setValue(user);
                    }
                } else {
                    userEntity.setValue(null);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors here
                Log.e("LogLog", "Error fetching user data: " + databaseError.getMessage());
                userEntity.setValue(null);
            }
        });

        return userEntity;
    }

    public LiveData<Boolean> updateUserInDatabase(UserEntity userEntity) {
        MutableLiveData<Boolean> state = new MutableLiveData<>(false);
        String userId = getCurrentUser().getUid();
        databaseReference.child(userId).setValue(userEntity)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        state.setValue(true);
                    } else {
                        state.setValue(false);
                    }
                });
        return state;
    }


}
