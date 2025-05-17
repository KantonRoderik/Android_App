package com.example.szakdolgozat.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import com.example.szakdolgozat.R;

public class NotificationReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "daily_reminder_channel";
    private static final String TAG = "NotificationReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Notification received");

        try {
            createNotificationChannel(context);
            Notification notification = buildNotification(context, intent);
            showNotification(context, notification);
        } catch (Exception e) {
            Log.e(TAG, "Error showing notification: " + e.getMessage());
        }
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    context.getString(R.string.notification_channel_name),
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            channel.setDescription(context.getString(R.string.notification_channel_desc));
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{0, 500, 200, 500});

            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
                Log.d(TAG, "Notification channel created");
            }
        }
    }

    private Notification buildNotification(Context context, Intent intent) {
        String time = intent.getStringExtra("time");
        String contentText = time != null ?
                String.format("Ne felejtsd el rögzíteni a mai étkezéseidet! (%s)", time) :
                "Ne felejtsd el rögzíteni a mai étkezéseidet!";

        return new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(contentText)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(contentText))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .build();
    }

    private void showNotification(Context context, Notification notification) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            int notificationId = (int) System.currentTimeMillis();
            manager.notify(notificationId, notification);
            Log.d(TAG, "Notification shown with id: " + notificationId);
        }
    }
}