package com.example.myapplication.present.ui.fragment.reminder;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.databinding.ItemReminderBinding;
import com.example.myapplication.domain.model.Reminder;
import com.example.myapplication.present.viewmodel.MyViewModel;

import java.util.List;

public class RemindAdapter extends RecyclerView.Adapter<RemindAdapter.ReminderViewHolder> {
    private final List<Reminder> reminderList;
    private final MyViewModel myViewModel;

    public RemindAdapter(List<Reminder> r, MyViewModel myViewModel) {
        this.reminderList = r;
        this.myViewModel = myViewModel;
    }

    @NonNull
    @Override
    public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemReminderBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_reminder,
                parent,
                false
        );
        return new ReminderViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderViewHolder holder, int position) {
        Reminder reminder = reminderList.get(position);
        holder.bind(reminder,myViewModel);

    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateReminders(List<Reminder> newReminders) {
        this.reminderList.clear();
        this.reminderList.addAll(newReminders);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return reminderList.size();
    }

    static class ReminderViewHolder extends RecyclerView.ViewHolder {
        private final ItemReminderBinding binding;

        public ReminderViewHolder(@NonNull ItemReminderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Reminder reminder,MyViewModel myViewModel) {
            binding.setReminder(reminder);
            binding.executePendingBindings();
            binding.cell1.setOnClickListener(v->{
                myViewModel.setMovieSelected(reminder.getMovieModel());
            });
        }
    }
}
