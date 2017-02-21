package apps.yuesaka.com.thehumanprojectfitnessapp;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.sql.Time;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * A broadcast receiver that reminds the user to walk at the top of every hour.
 */
public class WalkReminderReceiver extends BroadcastReceiver {
    private static final int WALKING_REMINDER_NOTIFICATION_ID = 2;
    // The app's AlarmManager, which provides access to the system alarm services.
    private AlarmManager alarmMgr;
    // The pending intent that is triggered when the alarm fires.
    private PendingIntent alarmIntent;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && intent.getAction() != null && intent.getAction().equals
                (WalkReminderReceiver.class.getSimpleName
                ())) {
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(android.R.drawable.sym_def_app_icon)
                            .setContentTitle(context.getString(R.string.walking_reminder))
                            .setContentText(context.getString(R.string.walking_reminder_message))
                            .setSound(alarmSound);

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
        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context,
                WalkReminderReceiver.class).setAction(WalkReminderReceiver.class.getSimpleName());
        alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        int hoursOfDay = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int nextHourOfDay = (hoursOfDay + 1 ) % 24;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, nextHourOfDay);
        calendar.set(Calendar.MINUTE, 0);
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(), AlarmManager.INTERVAL_HOUR,
                alarmIntent);
    }

    public void cancelAlarm(Context context) {
        if (alarmMgr!= null) {
            alarmMgr.cancel(alarmIntent);
        }
    }
}
