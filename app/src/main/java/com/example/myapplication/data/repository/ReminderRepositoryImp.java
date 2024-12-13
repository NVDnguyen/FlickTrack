package com.example.myapplication.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.example.myapplication.data.mapper.Mapper;
import com.example.myapplication.data.source.local.dao.ReminderDao;
import com.example.myapplication.data.source.local.entity.ReminderEntity;
import com.example.myapplication.domain.model.Reminder;
import com.example.myapplication.domain.repository.ReminderRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ReminderRepositoryImp implements ReminderRepository {
    private final Mapper mapper;
    private final ReminderDao reminderDao;
    private final Executor executor;

    @Inject
    public ReminderRepositoryImp(Mapper mapper, ReminderDao reminderDao) {
        this.mapper = mapper;
        this.reminderDao = reminderDao;
        this.executor = Executors.newSingleThreadExecutor(); // Executes tasks in a background thread
    }

    @Override
    public void insertReminder(Reminder reminder) {
        executor.execute(() -> reminderDao.insertReminder(mapper.mapReminderToReminderEntity(reminder)));
    }

    @Override
    public void updateReminder(Reminder reminder) {
        executor.execute(
                () ->{
                    reminderDao.updateReminder(mapper.mapReminderToReminderEntity(reminder));
                }

        );
    }

    @Override
    public void deleteReminder(Reminder reminder) {
        executor.execute(() -> reminderDao.deleteReminder(mapper.mapReminderToReminderEntity(reminder)));
    }

    @Override
    public void deleteReminderByID(int id) {
        executor.execute(()-> reminderDao.deleteReminderById(id));
    }


    @Override
    public void deleteAllReminders() {
        executor.execute(reminderDao::deleteAllReminders);
    }

    @Override
    public LiveData<List<Reminder>> getAllReminders() {
        LiveData<List<ReminderEntity>> reminderEntities = reminderDao.getAllReminders();
        return Transformations.map(reminderEntities, reminderEntityList -> {
            List<Reminder> reminderList = new ArrayList<>();
            for (ReminderEntity reminderEntity : reminderEntityList) {
                reminderList.add(mapper.mapReminderEntityToReminder(reminderEntity));
            }
            return reminderList;
        });
    }

    @Override
    public LiveData<Reminder> getReminderById(int movieId) {
        LiveData<ReminderEntity> reminderEntityLiveData = reminderDao.getReminderById(movieId);
        return Transformations.map(reminderEntityLiveData, mapper::mapReminderEntityToReminder);
    }

    @Override
    public LiveData<List<Reminder>> getUpcomingReminders() {
        LiveData<List<ReminderEntity>> reminderEntities = reminderDao.getUpcomingReminders();
        return Transformations.map(reminderEntities, reminderEntityList -> {
            List<Reminder> reminderList = new ArrayList<>();
            for (ReminderEntity reminderEntity : reminderEntityList) {
                reminderList.add(mapper.mapReminderEntityToReminder(reminderEntity));
            }
            return reminderList;
        });
    }
}
