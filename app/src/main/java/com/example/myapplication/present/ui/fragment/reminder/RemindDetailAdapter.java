package com.example.myapplication.present.ui.fragment.reminder;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.databinding.ItemReminderDetailBinding;
import com.example.myapplication.domain.model.Reminder;
import com.example.myapplication.present.viewmodel.MyViewModel;

import java.util.List;

public class RemindDetailAdapter extends RecyclerView.Adapter<RemindDetailAdapter.ReminderViewHolder> {
    private final List<Reminder> reminderList;
    private final MyViewModel myViewModel;    // Constructor
    public RemindDetailAdapter( MyViewModel myViewModel,List<Reminder> reminderList) {
        this.reminderList = reminderList;
        this.myViewModel = myViewModel;
    }

    @NonNull
    @Override
    public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemReminderDetailBinding binding = DataBindingUtil.inflate(LayoutInflater.from(
                parent.getContext())
                ,R.layout.item_reminder_detail,
                parent,
                false);
        return new ReminderViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderViewHolder holder, int position) {
        // Get the current reminder object
        Reminder reminder = reminderList.get(position);
        holder.bind(reminder,myViewModel);

    }
    @SuppressLint("NotifyDataSetChanged")
    public void update(List<Reminder> reminderList){
        this.reminderList.clear();
        this.reminderList.addAll(reminderList);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return reminderList.size();
    }

    // ViewHolder class
    public static class ReminderViewHolder extends RecyclerView.ViewHolder {
        ItemReminderDetailBinding binding;

        public ReminderViewHolder(@NonNull ItemReminderDetailBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        public void bind(Reminder reminder,MyViewModel myViewModel){
            binding.setReminder(reminder);
            binding.remindCardInfo.setOnClickListener(v->{
                myViewModel.setMovieSelected(reminder.getMovieModel());
            });
            binding.removeReminder.setOnClickListener(v -> {
                // Create an AlertDialog to confirm deletion
                new AlertDialog.Builder(v.getContext())
                        .setTitle("Confirmation")
                        .setMessage("Are you sure you want to delete this reminder?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            // Perform deletion
                            myViewModel.getReminderUseCase().deleteReminder(reminder);
                            Toast.makeText(v.getContext(), "Reminder deleted!", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> {
                            // Close the dialog when "Cancel" is pressed
                            dialog.dismiss();
                        })
                        .show();
            });

        }
    }
}
