package com.example.myapplication.present.ui.fragment.reminder;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.example.myapplication.databinding.FragmentAllReminderBinding;
import com.example.myapplication.domain.model.Reminder;
import com.example.myapplication.present.viewmodel.MyViewModel;

import java.util.ArrayList;
import java.util.List;

public class ReminderListFragment extends Fragment {
    private RemindDetailAdapter adapter;

    public ReminderListFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FragmentAllReminderBinding binding = FragmentAllReminderBinding.inflate(inflater,container,false);
        MyViewModel myViewModel = new ViewModelProvider(getActivity()).get(MyViewModel.class);
        // Initialize the RecyclerView and Database helper
        binding.recyclerShowNotify.setLayoutManager( new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false));
        adapter = new RemindDetailAdapter(myViewModel,new ArrayList<>());
        binding.recyclerShowNotify.setAdapter(adapter);
        myViewModel.getReminderUseCase().getAllReminders().observe(getViewLifecycleOwner(), new Observer<List<Reminder>>() {
            @Override
            public void onChanged(List<Reminder> reminders) {
                Log.d("changeLog","ReminderListFragment > Reminder changed :"+reminders.size());
                adapter.update(reminders);
            }
        });
        // add call back for back press
        NavController  navController = NavHostFragment.findNavController(this);
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                navController.popBackStack(R.id.listMovieFragment,false);
                myViewModel.setToolBarForHome(true);
            }
        });
        binding.iconBack.setOnClickListener(v->{
            navController.popBackStack(R.id.listMovieFragment,false);
            myViewModel.setToolBarForHome(true);
        });

        return binding.getRoot();
    }

}
