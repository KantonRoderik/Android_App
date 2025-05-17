package com.example.szakdolgozat.notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import androidx.annotation.RequiresApi;
import java.util.Calendar;

public class NotificationScheduler {
    private static final String TAG = "NotificationScheduler";

    public static void scheduleExactAlarms(Context context) {
        if (checkExactAlarmPermission(context)) {
            setExactAlarm(context, 9, 0, 1001);  // Reggel
            setExactAlarm(context, 13, 0, 1002); // Délben
            setExactAlarm(context, 20, 0, 1003); // Este


            setExactAlarm(context, 17, 37, 1004); // TESZT
            setExactAlarm(context, 17, 38, 1005); // TESZT
            setExactAlarm(context, 17, 39, 1006); // TESZT
        }
    }

    private static boolean checkExactAlarmPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (!am.canScheduleExactAlarms()) {
                requestExactAlarmPermission(context);
                return false;
            }
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    private static void requestExactAlarmPermission(Context context) {
        try {
            Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Failed to request exact alarm permission: " + e.getMessage());
        }
    }

    private static void setExactAlarm(Context context, int hour, int minute, int requestCode) {
        try {
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, NotificationReceiver.class);
            intent.putExtra("time", String.format("%02d:%02d", hour, minute));

            PendingIntent pi = PendingIntent.getBroadcast(
                    context,
                    requestCode,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            Calendar calendar = getNextAlarmTime(hour, minute);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);
            } else {
                am.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);
            }

            Log.i(TAG, "Alarm set for " + hour + ":" + minute);
        } catch (SecurityException e) {
            Log.e(TAG, "SecurityException: " + e.getMessage());
        }
    }

    private static Calendar getNextAlarmTime(int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        return calendar;
    }

    public static void cancelAllAlarms(Context context) {
        for (int requestCode : new int[]{1001, 1002, 1003}) {
            cancelAlarm(context, requestCode);
        }
    }

    private static void cancelAlarm(Context context, int requestCode) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_CANCEL_CURRENT
        );

        if (pi != null) {
            am.cancel(pi);
            pi.cancel();
            Log.i(TAG, "Cancelled alarm with requestCode: " + requestCode);
        }
    }
}