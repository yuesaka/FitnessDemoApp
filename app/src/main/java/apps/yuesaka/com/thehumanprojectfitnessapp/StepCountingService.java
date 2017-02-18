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
import android.util.Log;
import android.widget.Toast;

public class StepCountingService extends Service implements SensorEventListener {
    private static final String TAG = StepCountingService.class.getSimpleName();
    private static final int ONGOING_NOTIFICATION_ID = 1;

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

    @Override
    public IBinder onBind(Intent intent) {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_FASTEST);

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

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
