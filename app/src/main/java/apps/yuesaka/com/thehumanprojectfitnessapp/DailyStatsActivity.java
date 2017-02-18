package apps.yuesaka.com.thehumanprojectfitnessapp;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class DailyStatsActivity extends ActionBarActivity {

    private TextView stepsTakenText;
    private TextView usernameText;

    private SessionManager sessionManager;

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
}
