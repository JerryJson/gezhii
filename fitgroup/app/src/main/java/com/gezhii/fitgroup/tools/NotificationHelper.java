package com.gezhii.fitgroup.tools;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import com.gezhii.fitgroup.R;

import java.util.Calendar;

/**
 * Created by xianrui on 15/10/23.
 */
public class NotificationHelper {

    public static final int NOTIFICATION_ID = 10086;

    public static void createNotification(Context context, String title, String text, String ticker, Intent intent) {
        // BEGIN_INCLUDE(notificationCompat)
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        // END_INCLUDE(notificationCompat)

        // BEGIN_INCLUDE(intent)
        //Create Intent to launch this Activity again if the notification is clicked.

//        Class c = intent.getComponent().getClassName();

        PendingIntent pi = null;
//        if (Activity.class.isAssignableFrom(c)) {
        pi = PendingIntent.getActivity(context, (int) (Calendar.getInstance().getTimeInMillis() % 100), intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
//        } else if (Service.class.isAssignableFrom(c)) {
//            pi = PendingIntent.getService(context, (int) (Calendar.getInstance().getTimeInMillis() % 100), intent,
//                    PendingIntent.FLAG_UPDATE_CURRENT);
//        }

        builder.setContentIntent(pi);
        // END_INCLUDE(intent)

        // BEGIN_INCLUDE(ticker)
        // Sets the ticker text
        builder.setTicker(ticker);

        builder.setWhen(System.currentTimeMillis());

        builder.setContentTitle(title);

        builder.setContentText(text);

        // Sets the small icon for the ticker
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));
        // END_INCLUDE(ticker)

        // BEGIN_INCLUDE(buildNotification)
        // Cancel the notification when clicked
        builder.setAutoCancel(true);

        // Build the notification
        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_SHOW_LIGHTS;
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notification.defaults |= Notification.DEFAULT_LIGHTS;
        notification.defaults |= Notification.DEFAULT_SOUND;
        // END_INCLUDE(buildNotification)
        // START_INCLUDE(notify)
        // Use the NotificationManager to show the notification
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(NOTIFICATION_ID, notification);
        // END_INCLUDE(notify)
    }
}
