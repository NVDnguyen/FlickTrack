package com.example.myapplication.present.utils;

import static com.example.myapplication.app.Constants.CHANNEL_ID;
import static com.example.myapplication.app.Constants.URL_IMG;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.myapplication.R;
import com.example.myapplication.domain.model.Reminder;
import com.example.myapplication.present.ui.activity.MainActivity;
import com.squareup.picasso.Picasso;

public class NotificationWorker extends Worker {
    private static final int NOTIFICATION_ICON = R.mipmap.icon_app;
    private final Context context;
    public NotificationWorker(Context context, WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;

    }

    @NonNull
    @Override
    public Result doWork() {
        try{
            Reminder reminder = Reminder.fromJson(getInputData().getString("reminder"));
            Uri uri = Uri.parse(URL_IMG+reminder.getPoster_path());
            Bitmap bitmap = Picasso.get().load(uri).get();
            sendNotification(reminder,bitmap);
        }
        catch (Exception e){
            Log.d("changeLog","send notify" +e.toString());

        }
        return Result.success();


    }




    // Method to send notification
    private void sendNotification(Reminder reminder,Bitmap image) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Reminder Notifications", NotificationManager.IMPORTANCE_HIGH);
        notificationManager.createNotificationChannel(channel);


        // Build notification
        @SuppressLint("DefaultLocale") NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(NOTIFICATION_ICON)
                .setContentTitle(reminder.getTitle()+"\t"+reminder.getYearRelease().substring(0,4))
                .setContentText( reminder.getHour() + ":" + reminder.getMinute() + ": " + reminder.getDay() + "/" + reminder.getMonth() + "/" + reminder.getYear()+"\t Rate:"+String.format(" %.1f",Double.parseDouble(reminder.getRate())) + "/10")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setLargeIcon(image)
                .setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(image)
                        .bigLargeIcon((Bitmap) null));

        // Intent to open MainActivity when notification is clicked
        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.putExtra("pending_reminder",reminder);
        // avoid  create instance of MainActivity
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        // PendingIntent
        PendingIntent contentIntent = PendingIntent.getActivity(
                context,
                reminder.getMovieId(),
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        builder.setContentIntent(contentIntent);

        notificationManager.notify(reminder.getMovieId(), builder.build());
    }


}
