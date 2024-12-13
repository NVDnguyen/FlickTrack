package com.example.myapplication.domain.repository;


import androidx.lifecycle.LiveData;

import com.example.myapplication.domain.model.Reminder;

import java.util.List;

public interface ReminderRepository {
    void insertReminder(Reminder reminder);

    void updateReminder(Reminder reminder);

    void deleteReminder(Reminder reminder);
    void deleteReminderByID(int id);

    LiveData<List<Reminder>> getAllReminders();

    LiveData<Reminder> getReminderById(int movieId);

    LiveData<List<Reminder>>  getUpcomingReminders();
    void deleteAllReminders();
}
