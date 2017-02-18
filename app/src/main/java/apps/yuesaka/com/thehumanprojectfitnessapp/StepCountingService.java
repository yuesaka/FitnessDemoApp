package apps.yuesaka.com.thehumanprojectfitnessapp;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class StepCountingService extends Service implements SensorEventListener {
    private static final String TAG = StepCountingService.class.getSimpleName();
    private static final int ONGOING_NOTIFICATION_ID = 1;

    private SessionManager sessionManager;

    private DatabaseHelper dbHelper;

    public class StepCountingServiceBinder extends Binder {
        StepCountingService getService() {
            return StepCountingService.this;
        }
    }
    private IBinder stepCountingServiceBinder = new StepCountingServiceBinder();

    private SensorManager sensorManager;
    private Sensor stepSensor;
    private long steps = 0;
    private long previous_steps = 0;
    private long counterStepsSinceRegistration = 0;

    // For notifying the Activity about new steps.
    LocalBroadcastManager newStepBroadcaster;
    static final public String STEP_COUNT_UPDATE
            = "apps.yuesaka.com.thehumanprojectfitnessapp.STEP_COUNT_UPDATE";

    WalkReminderReceiver walkReminderAlarmReceiver;
    StepLogUpdateReceiver stepLogUpdateAlarmReceiver;

    public void broadcastStepCount() {
        Intent intent = new Intent(STEP_COUNT_UPDATE);
        newStepBroadcaster.sendBroadcast(intent);
    }

    @Override
    public void onCreate() {
        dbHelper = DatabaseHelper.getInstance(getApplicationContext());
        sessionManager = new SessionManager(getApplicationContext());
        walkReminderAlarmReceiver = new WalkReminderReceiver();
        walkReminderAlarmReceiver.setAlarm(this);
        stepLogUpdateAlarmReceiver = new StepLogUpdateReceiver();
        stepLogUpdateAlarmReceiver.setAlarm(this);
    }

    @Override
    public void onDestroy() {
        sensorManager.unregisterListener(this, stepSensor);
        walkReminderAlarmReceiver.cancelAlarm(this);
        stepLogUpdateAlarmReceiver.cancelAlarm(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_FASTEST);

        newStepBroadcaster = LocalBroadcastManager.getInstance(this);
        broadcastStepCount();

        return stepCountingServiceBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent notificationIntent = new Intent(this, DailyStatsActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification = new Notification.Builder(this)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.step_counting_notification_text))
                .setSmallIcon(R.drawable.abc_tab_indicator_material)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(ONGOING_NOTIFICATION_ID, notification);

        return START_STICKY;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        float[] values = event.values;
        int value = -1;

        if (values.length > 0) {
            value = (int) values[0];
        }
        if (sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            //Since it will return the total number since we registered we need to subtract the initial amount
            //for the current steps since we opened app
            if (counterStepsSinceRegistration < 1) {
                // initial value since registration
                counterStepsSinceRegistration = (int)values[0];
            }
            // Calculate steps taken based on first counter value received.
            steps = (int)values[0] - counterStepsSinceRegistration;
            long step_increment = steps - previous_steps;
            Log.d(TAG, "service step_increment: " + step_increment);
            dbHelper.updateStepsToday(dbHelper.getUserId(sessionManager.getSessionUsername()), (int) step_increment);
            previous_steps = steps;
            broadcastStepCount();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
