package apps.yuesaka.com.thehumanprojectfitnessapp;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * This activity is used to log into the app. If the user is already logged in, it will skip into
 * the daily statistic activity.
 */
public class LoginActivity extends ActionBarActivity {

    private Button loginButton;
    private Button newUserButton;
    private EditText userName;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if(getResources().getBoolean(R.bool.portrait_only)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        final SessionManager sessionManager  = new SessionManager(getApplicationContext());

        userName = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        newUserButton = (Button) findViewById(R.id.new_user_button);
        newUserButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent myIntent = new Intent(LoginActivity.this,
                        NewUserActivity.class);
                startActivity(myIntent);
            }
        });
        loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                if (!Utility.isRequiredFieldFilled(LoginActivity.this, userName) ||
                        !Utility.isRequiredFieldFilled(LoginActivity.this, password)) {
                    return;
                }

                DatabaseHelper dbInstance = DatabaseHelper.getInstance(LoginActivity.this);
                String usernameString = userName.getText().toString();
                String passwordString = password.getText().toString();
                if (!dbInstance.usernameExists(usernameString)) {
                    userName.setError(getText(R.string.edit_text_no_such_username));
                    return;
                } else if (!dbInstance.isCorrectPassword(usernameString, passwordString)) {
                    password.setError(getText(R.string.edit_text_incorrect_password));
                    return;
                }
                sessionManager.createLoginSession(usernameString);
                Intent intent = new Intent(LoginActivity.this,
                        DailyStatsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
        if (sessionManager.isLoggedIn()) {
            Intent intent = new Intent(LoginActivity.this,
                    DailyStatsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }
}
