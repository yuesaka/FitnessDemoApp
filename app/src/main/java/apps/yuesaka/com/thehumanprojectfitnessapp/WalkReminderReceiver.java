package apps.yuesaka.com.thehumanprojectfitnessapp;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.sql.Time;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * A broadcast receiver that reminds the user to walk every hour.
 */
public class WalkReminderReceiver extends BroadcastReceiver {
    private static final int WALKING_REMINDER_NOTIFICATION_ID = 2;
    // The app's AlarmManager, which provides access to the system alarm services.
    private AlarmManager alarmMgr;
    // The pending intent that is triggered when the alarm fires.
    private PendingIntent alarmIntent;
    boolean isSet= false;
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
        if (!isSet) {
            alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context,
                    WalkReminderReceiver.class).setAction(WalkReminderReceiver.class.getSimpleName());
            alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
            long timeTillNextHour = AlarmManager.INTERVAL_HOUR - (Calendar.getInstance().get(Calendar.MINUTE) * 60 * 1000);
            alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + timeTillNextHour, AlarmManager.INTERVAL_HOUR,
                    alarmIntent);
            isSet = true;
        }
    }

    public void cancelAlarm(Context context) {
        if (alarmMgr!= null) {
            alarmMgr.cancel(alarmIntent);
        }
    }
}
