package apps.yuesaka.com.thehumanprojectfitnessapp;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class NewUserActivity extends ActionBarActivity {
    private Button submitButton;
    private EditText newUserUserName;
    private EditText newUserPassword;
    private EditText newUserHeight;
    private RadioGroup newUserSex;
    private RadioButton newUserSexSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);
        if(getResources().getBoolean(R.bool.portrait_only)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        final SessionManager sessionManager  = new SessionManager(getApplicationContext());

        submitButton = (Button) findViewById(R.id.new_user_submit_button);
        newUserUserName = (EditText) findViewById(R.id.new_user_username);
        newUserPassword = (EditText) findViewById(R.id.new_user_password);
        newUserHeight = (EditText) findViewById(R.id.new_user_height);
        newUserSex = (RadioGroup) findViewById(R.id.new_user_sex_radio_group);

        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                if (!Utility.isRequiredFieldFilled(NewUserActivity.this, newUserUserName) ||
                        !Utility.isRequiredFieldFilled(NewUserActivity.this, newUserPassword) ||
                        !Utility.isRequiredFieldFilled(NewUserActivity.this, newUserHeight)) {
                    return;
                }
                int selectedId = newUserSex.getCheckedRadioButtonId();
                newUserSexSelected = (RadioButton) findViewById(selectedId);

                DatabaseHelper dbInstance = DatabaseHelper.getInstance(NewUserActivity.this);
                String newUserUsernameString = newUserUserName.getText().toString();
                if (dbInstance.usernameExists(newUserUsernameString)) {
                    newUserUserName.setError(getText(R.string.edit_text_username_taken));
                    return;
                }
                dbInstance.insertUser(newUserUsernameString, newUserPassword.getText().toString(), Integer
                                .valueOf(newUserHeight.getText().toString()),
                        newUserSexSelected.getText().toString());
                sessionManager.createLoginSession(newUserUsernameString);
                Intent intent = new Intent(NewUserActivity.this,
                        DailyStatsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }
}
