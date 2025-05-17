package com.example.szakdolgozat.notification;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                if (!alarmManager.canScheduleExactAlarms()) {
                    Log.w(TAG, "Cannot schedule exact alarms, permission missing");
                    return;
                }
            }
            Log.d(TAG, "Device boot completed, scheduling alarms");
            NotificationScheduler.scheduleExactAlarms(context);
        }
    }
}