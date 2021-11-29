package com.figurehowto.mirakl;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static com.figurehowto.mirakl.App.CHANNEL_ID;

public class AlertReciever extends BroadcastReceiver {
    private NotificationManagerCompat  notificationManager;
    @Override
    public void onReceive(Context context, Intent intent) {
        String contentText = intent.getExtras().getString("notifText");
        notificationManager = NotificationManagerCompat.from(context);
        Notification notification = new NotificationCompat.Builder(context,CHANNEL_ID)
                .setSmallIcon(R.drawable.notes_icon)
                .setContentText("Remember Your Todo"+"\""+ contentText +"\"")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .build();

        notificationManager.notify(1,notification);
    }
}
