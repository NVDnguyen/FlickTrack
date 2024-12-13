package com.example.myapplication.domain.usecase.uc;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.example.myapplication.domain.model.Reminder;
import com.example.myapplication.domain.repository.ReminderRepository;
import com.example.myapplication.present.utils.NotificationWorker;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

public class GetReminderUseCase {
    private final ReminderRepository reminderRepository;
    @Inject
    public GetReminderUseCase(ReminderRepository reminderRepository) {
        this.reminderRepository = reminderRepository;
    }
    public void insertReminder(Reminder reminder){
        reminderRepository.insertReminder(reminder);
    }

    public void deleteReminderByID(int id){
        reminderRepository.deleteReminderByID(id);
    }
    public void updateOrInsertToRemind(Reminder reminder, Context context, LifecycleOwner lifecycleOwner) {
        // Make sure the LiveData is of the correct type (Reminder)
        LiveData<Reminder> reminderLiveData = reminderRepository.getReminderById(reminder.getMovieId());

        // Observe the LiveData for changes
        reminderLiveData.observe(lifecycleOwner, new Observer<Reminder>() {
            @Override
            public void onChanged(Reminder r) {
                // Unsubscribe to prevent further updates after receiving data
                reminderLiveData.removeObserver(this);

                // Proceed only if the data is available
                if (r != null) {
                    // If the reminder exists and differs, update it
                    if (!r.equals(reminder)) {
                        reminderRepository.updateReminder(reminder);
                        Log.d("changeLog", "This reminder is available >> " + r.toString());
                        WorkManager.getInstance(context).cancelAllWorkByTag(reminder.getMovieId() + "");
                        setAlarm(reminder, context, lifecycleOwner);
                    }
                } else {
                    // If no reminder is found, insert it as a new entry
                    reminderRepository.insertReminder(reminder);
                    Log.d("changeLog", "This reminder inserted as new");
                    setAlarm(reminder, context, lifecycleOwner);
                }
            }
        });
    }



    public void deleteReminder(Reminder reminder){
        reminderRepository.deleteReminder(reminder);
        Log.d("changeLog",">> delete reminder "+reminder.getTitle());

    }

    public LiveData<List<Reminder>> getAllReminders(){
        return  reminderRepository.getAllReminders();
    }

    public LiveData<Reminder> getReminderById(int id){
        return reminderRepository.getReminderById(id);
    }

    public LiveData<List<Reminder>> getUpcomingReminders(){
        return reminderRepository.getUpcomingReminders();
    }
    public void deleteAllReminders(){
        reminderRepository.deleteAllReminders();
    }

    @SuppressLint("ScheduleExactAlarm")
    public void setAlarm(Reminder reminder, Context context, LifecycleOwner lifecycleOwner) {
        @SuppressLint("RestrictedApi") Data inputData = new Data.Builder()
                .putString("reminder",Reminder.toJson(reminder))
                .build();
        // Create a one-time work request
        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(NotificationWorker.class)
                .setInitialDelay(calculateDelay(reminder), TimeUnit.MILLISECONDS)
                .setInputData(inputData)
                .addTag(reminder.getMovieId()+"")
                .build();
        // Enqueue the work with WorkManager
        WorkManager.getInstance(context)
                .getWorkInfoByIdLiveData(workRequest.getId())
                .observe(lifecycleOwner, workInfo -> {
                    if (workInfo != null && workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                        // Call back here or notify that work is done
                        deleteReminder(reminder);
                        Log.d("changeLog","Done => worker -> reminder ="+reminder.toString());
                    }else{
                        Log.d("changeLog","Callback => worker -> reminder ="+reminder.toString());

                    }

                });
        WorkManager.getInstance(context)
                .enqueue(workRequest);


    }

    private long calculateDelay(Reminder reminder) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(reminder.getYear(), reminder.getMonth()-1 , reminder.getDay(),
                reminder.getHour(), reminder.getMinute(), 0);
        calendar.setTimeZone(TimeZone.getDefault());

        long reminderTimeMillis = calendar.getTimeInMillis();
        long currentTimeMillis = System.currentTimeMillis();
        long delay = reminderTimeMillis - currentTimeMillis;
//        Log.d("LogLog", "Reminder time: " + calendar.getTime());
//        Log.d("LogLog", "Current time: " + new Date(currentTimeMillis));
//
//        Log.d("LogLog","calculateDelay -> delay ="+delay);

        return delay;

    }

}
