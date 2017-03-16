package in.meegotech.invisiblewidgetspro.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.res.ResourcesCompat;

import org.greenrobot.eventbus.EventBus;

import in.meegotech.invisiblewidgetspro.R;

/**
 * Created by shivam on 16/03/17.
 */

public class NotificationHelper extends BroadcastReceiver {
    private static final int notificationId = 786;

    public static class TurnOffConfigModeEvent {}

    @Override
    public void onReceive(Context context, Intent intent) {
        //This updates the configCard in both Main and Configuration Activity
        EventBus.getDefault().post(new TurnOffConfigModeEvent());

        //Sets config mode to false and hide all the widgets
        SharedPrefHelper.setConfigModeValue(context, false);
        UpdateWidgetHelper.hideWidgets(context);
    }

    //This method is called from Main and Configuration Activities only
    public static void showNotification(Context context) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_notification_small)
                        .setContentTitle(context.getString(R.string.notification_title))
                        .setContentText(context.getString(R.string.notification_text))
                        .setColor(ResourcesCompat.getColor(context.getResources(), R.color
                                .cyan_700, null))
                        //the notification gets cancelled on tapping/touch
                        .setAutoCancel(true)
                        //the notification doesn't get removed on swiping left/right
                        .setOngoing(true)
                        //the notification shows on top of all other notifications
                        .setPriority(Notification.PRIORITY_MAX);

        Intent intent = new Intent(context, NotificationHelper.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        mBuilder.setContentIntent(pendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService
                (Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(notificationId, mBuilder.build());
    }

    //This method is called from Main and Configuration Activities only
    public static void hideNotification(Context context) {

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService
                (Context.NOTIFICATION_SERVICE);

        mNotificationManager.cancelAll();
    }
}
