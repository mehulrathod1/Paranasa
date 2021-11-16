package com.ni.parnasa.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.os.Build;



import java.util.Random;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.ni.parnasa.R;

public class NotificationHelper extends ContextWrapper {

    private static final String CHANNEL_ID = "MyPickmeAppId";
    private static final String CHANNEL_NAME = "PickmeAppChannel";

    private static NotificationHelper instance;
    private static NotificationManager notificationManager;
    private NotificationManager managerCompact;

    public static NotificationHelper getInstance(Context mContext) {
        if (instance == null)
            instance = new NotificationHelper(mContext);

        return instance;
    }

    private NotificationManager getManager() {
        if (notificationManager == null)
            notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        return notificationManager;
    }

    private NotificationHelper(Context base) {
        super(base);
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setLightColor(Color.BLUE);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            channel.setShowBadge(true);
            getManager().createNotificationChannel(channel);
        }*/
    }

    public void notify(String title, String body, PendingIntent pendingIntent) {
        Notification.Builder builder = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setLightColor(Color.BLUE);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            channel.setShowBadge(true);
            getManager().createNotificationChannel(channel);

            builder = new Notification.Builder(getApplicationContext(), CHANNEL_ID);
            builder.setColorized(true);
            builder.setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE);

        } else {
            builder = new Notification.Builder(getApplicationContext());
        }

        builder.setContentTitle(title);
        builder.setContentText(body);
        builder.setAutoCancel(true);
        builder.setStyle(new Notification.BigTextStyle().bigText(body));
        builder.setSmallIcon(R.drawable.ic_notifications);
        builder.setVibrate(new Notification().vibrate);

        if (pendingIntent != null)
            builder.setContentIntent(pendingIntent);

        getManager().notify(new Random().nextInt(), builder.build());
    }

    public void removeNotification() {
        if (notificationManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationManager.deleteNotificationChannel(CHANNEL_ID);
            } else {
                notificationManager.cancelAll();
            }
        } else {
            throw new NullPointerException("Not having any notification");
        }
    }

    private Notification.Builder getNotigicationBuilder(String title, String body) {
        Notification.Builder builder = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new Notification.Builder(getApplicationContext(), CHANNEL_ID);
        } else {
            builder = new Notification.Builder(getApplicationContext());
        }

        builder.setContentTitle(title);
        builder.setContentText(body);
        builder.setSmallIcon(R.drawable.ic_notifications);
        builder.setAutoCancel(true);

        return builder;
    }

    public NotificationManagerCompat getManagerCompact() {
        return NotificationManagerCompat.from(getApplicationContext());
    }
}
