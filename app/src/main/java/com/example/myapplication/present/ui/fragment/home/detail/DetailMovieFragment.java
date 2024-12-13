package com.example.myapplication.present.ui.fragment.home.detail;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.databinding.FragmentDetailBinding;
import com.example.myapplication.domain.model.Movie;
import com.example.myapplication.domain.model.Reminder;
import com.example.myapplication.domain.model.Staff;
import com.example.myapplication.present.viewmodel.MyViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DetailMovieFragment extends Fragment {
    private MyViewModel myViewModel;
    private FragmentDetailBinding binding;
    private Movie movie;


    public DetailMovieFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDetailBinding.inflate(inflater, container, false);
        myViewModel = new ViewModelProvider(requireActivity()).get(MyViewModel.class);
        binding.recyclerViewCastCrew.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL,false));
        myViewModel.getMovieSelected().observe(getViewLifecycleOwner(), new Observer<Movie>() {
            @SuppressLint("CheckResult")
            @Override
            public void onChanged(Movie m) {
                if (m != null) {
                    if(m.isValid()){  // Check if the data is valid
                        movie =m;
                        initUI();
                    }else{
                        // Fetch from the server if the data is invalid
                        myViewModel.getMovieDetail(m).observe(getViewLifecycleOwner(), new Observer<Movie>() {
                            @Override
                            public void onChanged(Movie movie1) {
                                movie1.setFav(m.isFav());
                                movie = movie1;
                                initUI();
                            }
                        });
                    }
                }
            }
        });


        // Access NavController
        NavController navController = NavHostFragment.findNavController(this);
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                navController.navigateUp();
                myViewModel.setMovieSelected(null);
                myViewModel.setToolBarForHome(true);
            }
        });

        return binding.getRoot();
    }
    private void initUI(){
        binding.setMovie(movie);
        // set data for list cast and crew
        myViewModel.loadCastAndCrew(movie.getId()).observe(getViewLifecycleOwner(), new Observer<List<Staff>>() {
            @Override
            public void onChanged(List<Staff> staff) {
                ListStaffAdapter listStaffAdapter = new ListStaffAdapter(staff);
                binding.recyclerViewCastCrew.setAdapter(listStaffAdapter);
            }
        });
        // set action to update favorite
        binding.favoriteButton.setOnClickListener(v->{
            if(movie.isFav()){
                movie.setFav(false);
                binding.favoriteButton.setImageResource(R.drawable.star);
                myViewModel.deleteFavMovie(movie);
            }else{
                movie.setFav(true);
                binding.favoriteButton.setImageResource(R.drawable.star_filled);
                myViewModel.addFavMovie(movie);
            }

        });
        myViewModel.getMovieChanged().observe(getViewLifecycleOwner(), new Observer<Movie>() {
            @Override
            public void onChanged(Movie m) {
                if(m!=null && movie.getId()==m.getId()){
                    movie.setFav(m.isFav());
                    binding.favoriteButton.setImageResource(movie.isFav() ? R.drawable.star_filled: R.drawable.star );
                }
            }
        });
        // fetch reminder state
        fetchSqlite();
        // set action for button set remind
        binding.remindButton.setOnClickListener(v->{
            setRemind();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @SuppressLint("DefaultLocale")
    void setRemind() {
          // Create a dialog builder
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

            // Inflate custom layout
            View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_picktime, null);
            builder.setView(dialogView);

            // Initialize DatePicker and TimePicker from dialog layout
            DatePicker datePicker = dialogView.findViewById(R.id.date_picker);
            TimePicker timePicker = dialogView.findViewById(R.id.time_picker);
            timePicker.setIs24HourView(true); // Set 24-hour view

            // Get the current date and time
            Calendar now = Calendar.getInstance();

            // Set DatePicker to the current date
            datePicker.updateDate(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));

            // Set TimePicker to the current time
            timePicker.setHour(now.get(Calendar.HOUR_OF_DAY));
            timePicker.setMinute(now.get(Calendar.MINUTE));

            // Initialize the "DONE" button (TextView)
            TextView textView = dialogView.findViewById(R.id.button_edit);

            // Create the dialog
            AlertDialog alertDialog = builder.create();

            // Format for displaying the date and time
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM d, yyyy", Locale.ENGLISH);

            // Initialize the remindTextView where the date and time will be displayed
            TextView remindTextView = dialogView.findViewById(R.id.selected_datetime);

            // Function to update the text dynamically
            Runnable updateText = () -> {
                // Get the selected date from DatePicker
                int day = datePicker.getDayOfMonth();
                int month = datePicker.getMonth();
                int year = datePicker.getYear();

                // Get the selected time from TimePicker
                int hour = timePicker.getHour();
                int minute = timePicker.getMinute();

                // Update the calendar with the selected date and time
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, day, hour, minute);

                // Format the selected date and time
                String formattedDate = dateFormat.format(calendar.getTime());

                // Update the TextView with the formatted date and time
                remindTextView.setText(formattedDate);
            };

            // Set the initial text to the current date and time
            updateText.run();

            // Add listeners to update the text dynamically when the user changes the date or time
            datePicker.setOnDateChangedListener((view1, year, monthOfYear, dayOfMonth) -> updateText.run());
            timePicker.setOnTimeChangedListener((view12, hourOfDay, minute) -> updateText.run());

            // Set up the "DONE" button click listener
            textView.setOnClickListener(vv -> {
                // Get the selected date and time from DatePicker and TimePicker
                Calendar selectedTime = Calendar.getInstance();
                selectedTime.set(
                        datePicker.getYear(),
                        datePicker.getMonth(),
                        datePicker.getDayOfMonth(),
                        timePicker.getHour(),
                        timePicker.getMinute()
                );

                // Compare the selected time with the current time
                if (selectedTime.before(now)) {
                    // Show an error message if the selected time is in the past
                    Toast.makeText(getContext(), "Please select a future time for the reminder.", Toast.LENGTH_LONG).show();
                } else {
                    // Create the reminder object with valid future time
                    // public Reminder(int year, int month, int day, int hour, int minute, Movie movie)
                    Reminder reminder = new Reminder(
                            datePicker.getYear(),
                            datePicker.getMonth()+1,
                            datePicker.getDayOfMonth(),
                            timePicker.getHour(),
                            timePicker.getMinute(),
                            movie
                    );
                    // Callback to set the reminder
                    myViewModel.getReminderUseCase().updateOrInsertToRemind(reminder,getActivity(),getActivity());

                    Toast.makeText(getContext(), movie.getTitle() + "\n" + reminder.toString(), Toast.LENGTH_LONG).show();
                    // Dismiss the dialog
                    alertDialog.dismiss();
                }
            });

            alertDialog.show();

    }
    private void fetchSqlite(){
        binding.notificationCard.setVisibility(View.GONE);
        myViewModel.getReminderUseCase().getAllReminders().observe(getViewLifecycleOwner(), new Observer<List<Reminder>>() {
            @Override
            public void onChanged(List<Reminder> reminderList) {
                Log.d("changeLog","DetailMovieFragment  "+reminderList.size());

                for (Reminder r:
                     reminderList) {
                    if(r.getMovieId() == movie.getId()){
                        @SuppressLint("DefaultLocale") String datetime = String.format("%02d/%02d/%02d %02d:%02d",
                                r.getYear(),
                                r.getMonth(),
                                r.getDay(),
                                r.getHour(),
                                r.getMinute());
                        binding.notificationCard.setVisibility(View.VISIBLE);
                        binding.notificationMessage.setText(datetime);
                        return;
                    }

                }
                binding.notificationCard.setVisibility(View.GONE);

            }
        });

    }
}
