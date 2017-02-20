package apps.yuesaka.com.thehumanprojectfitnessapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import java.util.Calendar;

public class DailyResetReceiver extends WakefulBroadcastReceiver {
    // The app's AlarmManager, which provides access to the system alarm services.
    private AlarmManager alarmMgr;
    // The pending intent that is triggered when the alarm fires.
    private PendingIntent alarmIntent;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("DailyResetReceiver", "onReceive!");
        Log.d("DailyResetReceiver", "action: " + intent.getAction());
        if (intent != null && intent.getAction().equals(DailyResetReceiver.class.getSimpleName
                ())) {
            DatabaseHelper.getInstance(context.getApplicationContext()).resetDailyValues();
        }
    }

    public void setAlarm(Context context) {
        PendingIntent pendingIntent  = PendingIntent.getBroadcast(context, 0, new Intent(context,
                        DailyResetReceiver.class).setAction(DailyResetReceiver.class.getSimpleName()),
                PendingIntent
                .FLAG_UPDATE_CURRENT);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.HOUR, 11);
        calendar.set(Calendar.AM_PM, Calendar.PM);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                pendingIntent);
    }

    public void cancelAlarm(Context context) {
        if (alarmMgr!= null) {
            alarmMgr.cancel(alarmIntent);
        }
    }
}
