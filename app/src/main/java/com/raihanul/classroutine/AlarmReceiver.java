package com.raihanul.classroutine;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.SharedLibraryInfo;
import android.icu.text.DateFormat;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        PreferencesHelper pref = new PreferencesHelper(context);
        if (pref.getNotifySetting(true)) {
            boolean soundEnabled = pref.getRoutineSoundSetting(true);
            Intent i = new Intent(context, MainActivity.class);
            PendingIntent clickNotification = PendingIntent.getActivity(context, 1507038, i, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder nb = new NotificationCompat.Builder(context);
            nb.setSmallIcon(R.mipmap.ic_launcher);
            nb.setTicker(intent.getStringExtra(context.getResources().getString(R.string.intentDataKey_alarmTicker)));
            nb.setContentTitle(intent.getStringExtra(context.getResources().getString(R.string.intentDataKey_alarmTitle)));
            nb.setContentText(intent.getStringExtra(context.getResources().getString(R.string.intentDataKey_alarmText)));
            nb.setContentIntent(clickNotification);
            nb.setWhen(System.currentTimeMillis());
            nb.setAutoCancel(true);

            if (soundEnabled) {
                nb.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
            }

            NotificationManager nm = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
            nm.notify(0, nb.build());
        }
    }
}
