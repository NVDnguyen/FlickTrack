package com.example.myapplication.present.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.domain.model.Result;
import com.example.myapplication.present.viewmodel.MyViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {
    private MyViewModel myViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_splash);

        myViewModel = new ViewModelProvider(this).get(MyViewModel.class);

        // Initialize the splash screen animation
        ImageView imagePerson = findViewById(R.id.img);
        Glide.with(this)
                .asGif()
                .load(R.drawable.spash_screen_gif)
                .into(imagePerson);

        // Observe the result of the sign-in process
        myViewModel.getProfileUseCase().signIn("ryosan192@gmail.com", "123456")
                .observe(this, new Observer<Result<Boolean>>() {
                    @Override
                    public void onChanged(Result<Boolean> booleanResult) {
                        if (booleanResult != null) {
                            if (booleanResult.message != null && !booleanResult.message.isEmpty()) {
                                Log.d("SplashActivity", booleanResult.message);
                            } else {
                                Log.d("SplashActivity", "No message available.");
                            }
                        }
                    }
                });

        // Delay the transition to the main activity for 10 seconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intentMain = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intentMain);
                finish();
            }
        }, 5555);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove observers to avoid memory leaks
        myViewModel.getProfileUseCase().signIn("ryosan192@gmail.com", "123456").removeObservers(this);
    }
}
