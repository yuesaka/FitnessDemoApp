package apps.yuesaka.com.thehumanprojectfitnessapp;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * A broadcast receiver that reminds the user to walk every hour.
 */
public class WalkReminderReceiver extends BroadcastReceiver {
    private static final int WALKING_REMINDER_NOTIFICATION_ID = 2;
    // The app's AlarmManager, which provides access to the system alarm services.
    static private AlarmManager alarmMgr;
    // The pending intent that is triggered when the alarm fires.
    private PendingIntent alarmIntent;
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && intent.getAction() != null && intent.getAction().equals
                (WalkReminderReceiver.class.getSimpleName
                ())) {
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(android.R.drawable.sym_def_app_icon)
                            .setContentTitle("Walking Reminder")
                            .setContentText("Stand up and go walk!");

            // Sets an ID for the notification
            int mNotificationId = WALKING_REMINDER_NOTIFICATION_ID;
            // Gets an instance of the NotificationManager service
            NotificationManager mNotifyMgr =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            // Builds the notification and issues it.
            mNotifyMgr.notify(mNotificationId, mBuilder.build());
        }
    }

    public void setAlarm(Context context) {
        if (alarmMgr == null) {
            alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        }
        Intent intent = new Intent(context,
                WalkReminderReceiver.class).setAction(WalkReminderReceiver.class.getSimpleName());
        alarmIntent = PendingIntent.getBroadcast(context, 5300, intent, PendingIntent
                .FLAG_CANCEL_CURRENT);
        alarmMgr.cancel(PendingIntent.getBroadcast(context, 5300, intent, PendingIntent
                .FLAG_UPDATE_CURRENT));
        alarmMgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                0, Utility.HOUR_MILI, alarmIntent);
    }

    public void cancelAlarm(Context context) {
        if (alarmMgr!= null) {
            alarmMgr.cancel(alarmIntent);
        }
    }
}
