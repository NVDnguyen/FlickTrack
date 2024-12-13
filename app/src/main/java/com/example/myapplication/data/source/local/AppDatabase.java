package com.example.myapplication.data.source.local;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.myapplication.data.source.local.dao.MovieFavDao;
import com.example.myapplication.data.source.local.dao.ReminderDao;  // Import your ReminderDao
import com.example.myapplication.data.source.local.entity.MovieFavEntity;
import com.example.myapplication.data.source.local.entity.ReminderEntity;  // Import your ReminderEntity

@Database(entities = {MovieFavEntity.class, ReminderEntity.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract MovieFavDao movieFavDao();
    public abstract ReminderDao reminderDao();
}
