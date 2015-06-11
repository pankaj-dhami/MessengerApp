package com.example.pankaj.mychatapp.Utility;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.example.pankaj.mychatapp.R;
import com.example.pankaj.mychatapp.SplashScreen;
import com.microsoft.windowsazure.notifications.NotificationsHandler;

/**
 * Created by pankaj.dhami on 6/11/2015.
 */
public class MyHandler extends NotificationsHandler {

public static final int NOTIFICATION_ID = 1;
private NotificationManager mNotificationManager;
NotificationCompat.Builder builder;
Context ctx;

static public MyService mainActivity;

    @Override
    public void onReceive(Context context, Bundle bundle) {
        ctx = context;
        String nhMessage = bundle.getString("message");

        sendNotification(nhMessage);
        mainActivity.DialogNotify("Received Notification",nhMessage);
    }

    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager)
                ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0,
                new Intent(ctx, SplashScreen.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(ctx)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Notification Hub Demo")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}