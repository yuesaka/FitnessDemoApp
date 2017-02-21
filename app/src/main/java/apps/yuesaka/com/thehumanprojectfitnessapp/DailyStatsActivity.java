package apps.yuesaka.com.thehumanprojectfitnessapp;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This Activity displays the daily statistics for the current logged in user.
 */
public class DailyStatsActivity extends ActionBarActivity {

    private TextView stepsTakenText;
    private TextView usernameText;
    private TextView distanceText;
    private TextView distanceFeetText;
    private ListView stepStatsListView ;


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
        if(getResources().getBoolean(R.bool.portrait_only)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        PackageManager pm = getPackageManager();
        if (!pm.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_COUNTER)) {
            Toast.makeText(this, getString(R.string.sensor_lacking_message), Toast.LENGTH_LONG)
                    .show();
        }
        dbHelper = DatabaseHelper.getInstance(getApplicationContext());
        stepsTakenText = (TextView) findViewById(R.id.daily_stats_steps_taken);
        usernameText = (TextView) findViewById(R.id.daily_stats_greetings);
        distanceText = (TextView) findViewById(R.id.daily_stats_disntace_walked);
        distanceFeetText = (TextView) findViewById(R.id.daily_stats_disntace_walked_feet);
        stepStatsListView = (ListView) findViewById(R.id.step_stats_list);
        stepsTakenText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent dbmanager = new Intent(DailyStatsActivity.this, AndroidDatabaseManager
                        .class);
                startActivity(dbmanager);
            }
        });
        sessionManager = new SessionManager(getApplicationContext());
        if (!sessionManager.checkLogin()) {
            finish();
            return;
        }
        setupStepLogUI();
        updateStepAndDistanceText();
        usernameText.setText(getString(R.string.daily_stats_title, sessionManager
                .getSessionUsername(),
                Utility.getCurrentDateString()));

        // Listen for the change in steps from StepCountingService
        newStepReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (sessionManager.isLoggedIn()) {
                    updateStepAndDistanceText();
                }
            }
        };
    }

    private void setupStepLogUI() {
        List<Pair<String, Integer>> currentUserStepLog = dbHelper.getStepLogData(sessionManager
                .getSessionUsername());
        List<String> currentUserStepLogStrings = new ArrayList<>();
        for (Pair<String, Integer> pair : currentUserStepLog) {
            String stepLogString = new String();
            String dateString = Utility.getCurrentDateString();
            if (!pair.first.equals(dateString)) { // Don't display current date
                int userId = dbHelper.getUserId(sessionManager
                        .getSessionUsername());
                int stepsWalkedToday = dbHelper.getStepsToday(userId);
                double distance = Utility.stepsToMeter(stepsWalkedToday, dbHelper
                        .getUserHeight(userId), dbHelper.getUserSex(userId).equals(getString(R
                        .string.male_string)));
                stepLogString = getString(R.string.step_log_entry,pair.first,pair.second.toString
                        (),Utility.formatDouble(distance),
                        Utility.formatDouble(distance * Utility.METER_TO_FEET_CONVERSION));
                currentUserStepLogStrings.add(stepLogString);
            }
        }

        if (currentUserStepLogStrings.isEmpty()) {
            currentUserStepLogStrings.add(getString(R.string.nothing_logged));
        }
        ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                        currentUserStepLogStrings);
        stepStatsListView.setAdapter(itemsAdapter);

    }

    private void updateStepAndDistanceText() {
        int userId = dbHelper.getUserId(sessionManager
                .getSessionUsername());
        int stepsWalkedToday = dbHelper.getStepsToday(userId);
        stepsTakenText.setText(getString(R.string.steps_taken, stepsWalkedToday));
        double distance = Utility.stepsToMeter(stepsWalkedToday, dbHelper
                .getUserHeight(userId), dbHelper.getUserSex(userId).equals(getString(R
                .string.male_string)));
        distanceText.setText(getString(R.string.distance_walked_meter, Utility.formatDouble
                (distance)));
        distanceFeetText.setText(getString(R.string.distance_walked_feet, Utility.formatDouble
                (distance * Utility.METER_TO_FEET_CONVERSION)));
    }

    /**
     * Menu related functions
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent = null;
        switch (id) {
            case R.id.menu_logout:
                intent = new Intent(this, StepCountingService.class);
                stopService(intent);
                sessionManager.logoutUser();
                break;
            case R.id.menu_leaderboard:
                intent = new Intent(DailyStatsActivity.this,
                        LeaderBoardActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
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
