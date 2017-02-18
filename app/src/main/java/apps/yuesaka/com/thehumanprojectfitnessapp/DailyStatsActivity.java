package apps.yuesaka.com.thehumanprojectfitnessapp;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class DailyStatsActivity extends ActionBarActivity {

    private TextView stepsTakenText;
    private TextView usernameText;

    private SessionManager sessionManager;

    // Service related objects
    StepCountingService stepCountingService;
    boolean serviceBound = false;

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, StepCountingService.class);
        startService(intent);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (serviceBound) {
            unbindService(serviceConnection);
            serviceBound = false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_stats);
        stepsTakenText = (TextView) findViewById(R.id.daily_stats_steps_taken);
        usernameText = (TextView) findViewById(R.id.daily_stats_greetings);
        stepsTakenText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent dbmanager = new Intent(DailyStatsActivity.this, AndroidDatabaseManager
                        .class);
                startActivity(dbmanager);
            }
        });
        sessionManager = new SessionManager(getApplicationContext());
        usernameText.setText(sessionManager.getSessionUsername());
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
