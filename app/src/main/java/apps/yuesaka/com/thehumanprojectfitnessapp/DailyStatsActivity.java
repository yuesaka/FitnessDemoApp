package apps.yuesaka.com.thehumanprojectfitnessapp;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.text.DecimalFormat;

public class DailyStatsActivity extends ActionBarActivity {

    private TextView stepsTakenText;
    private TextView usernameText;
    private TextView distanceText;

    private SessionManager sessionManager;
    private DatabaseHelper dbHelper;

    // Service related objects
    StepCountingService stepCountingService;
    boolean serviceBound = false;

    // Receive update from StepCountingService about new steps
    BroadcastReceiver newStepReceiver;

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, StepCountingService.class);
        startService(intent);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        LocalBroadcastManager.getInstance(this).registerReceiver((newStepReceiver),
                new IntentFilter(StepCountingService.STEP_COUNT_UPDATE)
        );
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (serviceBound) {
            unbindService(serviceConnection);
            serviceBound = false;
        }
        LocalBroadcastManager.getInstance(this).unregisterReceiver(newStepReceiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_stats);
        dbHelper = DatabaseHelper.getInstance(getApplicationContext());
        stepsTakenText = (TextView) findViewById(R.id.daily_stats_steps_taken);
        usernameText = (TextView) findViewById(R.id.daily_stats_greetings);
        distanceText = (TextView) findViewById(R.id.daily_stats_disntace_walked);
        stepsTakenText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent dbmanager = new Intent(DailyStatsActivity.this, AndroidDatabaseManager
                        .class);
                startActivity(dbmanager);
            }
        });
        sessionManager = new SessionManager(getApplicationContext());
        updateStepAndDistanceText();
        usernameText.setText("Hello, " + sessionManager.getSessionUsername());

        // Listen for the change in steps from StepCountingService
        newStepReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateStepAndDistanceText();
            }
        };
    }

    private void updateStepAndDistanceText() {
        int userId = dbHelper.getUserId(sessionManager
                .getSessionUsername());
        int stepsWalkedToday = dbHelper.getStepsToday(userId);
        stepsTakenText.setText(Integer.toString(stepsWalkedToday) + " Steps");
        double distance = Utility.stepsToFoot(stepsWalkedToday, dbHelper
                .getUserHeight(userId), dbHelper.getUserSex(userId).equals(getString(R
                .string.male_string)));
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        distanceText.setText(decimalFormat.format(distance) + " Meters");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_logout:
                sessionManager.logoutUser();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // Service connection object
    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            StepCountingService.StepCountingServiceBinder myBinder = (StepCountingService.StepCountingServiceBinder) service;
            stepCountingService = myBinder.getService();
            serviceBound = true;
        }
    };
}
