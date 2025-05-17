package com.example.szakdolgozat.helpers;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.szakdolgozat.R;

public class NotificationWorker extends Worker {

    public NotificationWorker(
            @NonNull Context context,
            @NonNull WorkerParameters workerParams
    ) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        // Itt hozd létre és jelenítsd meg az értesítést
        showNotification(getApplicationContext());
        return Result.success();
    }

    private void showNotification(Context context) {
        android.app.NotificationManager notificationManager =
                (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // NotificationChannel létrehozása (Android 8.0+)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            android.app.NotificationChannel channel = new android.app.NotificationChannel(
                    "daily_reminder",
                    "Daily Reminders",
                    android.app.NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(channel);
        }

        // Értesítés létrehozása
        androidx.core.app.NotificationCompat.Builder builder =
                new androidx.core.app.NotificationCompat.Builder(context, "daily_reminder")
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle("Kalóriabevitel figyelő")
                        .setContentText("Ne felejtsd el rögzíteni a mai étkezéseidet!")
                        .setPriority(androidx.core.app.NotificationCompat.PRIORITY_DEFAULT);

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }
}