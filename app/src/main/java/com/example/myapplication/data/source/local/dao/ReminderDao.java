package com.example.myapplication.data.source.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myapplication.data.source.local.entity.ReminderEntity;

import java.util.List;

@Dao
public interface ReminderDao {

    @Insert
    void insertReminder(ReminderEntity reminder);

    @Update
    void updateReminder(ReminderEntity reminder);
    @Query("DELETE FROM reminders WHERE movieId = :id")
    void deleteReminderById(int id);

    @Delete
    void deleteReminder(ReminderEntity reminder);

    @Query("SELECT * FROM reminders")
    LiveData<List<ReminderEntity>> getAllReminders();

    @Query("SELECT * FROM reminders WHERE movieId = :id")
    LiveData<ReminderEntity> getReminderById(int id);

    @Query("DELETE FROM reminders")
    void deleteAllReminders();


    @Query("SELECT * FROM reminders ORDER BY " +
            "strftime('%s', year || '-' || month || '-' || day || ' ' || hour || ':' || minute) ASC " +
            "LIMIT 3")
    LiveData<List<ReminderEntity>> getUpcomingReminders();
}
