package apps.yuesaka.com.thehumanprojectfitnessapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

/**
 * A class that manages the session (i.e. stores active user info in the shared preference of the
 * app)
 */
public class SessionManager {
    SharedPreferences sharedPreferences;

    SharedPreferences.Editor editor;

    Context context;

    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREFERENCE_NAME = "sessionData";
    private static final String IS_LOGGED_IN = "IsLoggedIn";
    public static final String KEY_USERNAME = "name";

    public SessionManager(Context context){
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, PRIVATE_MODE);
        editor = sharedPreferences.edit();
    }

    public void createLoginSession(String username){
        editor.putBoolean(IS_LOGGED_IN, true);
        editor.putString(KEY_USERNAME, username);
        // commit changes
        editor.commit();
    }

    // Returns the username of the current logged-in user.
    public String getSessionUsername(){
        return sharedPreferences.getString(KEY_USERNAME, null);
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(IS_LOGGED_IN, false);
    }

    // Checks to see if a user is logged in. Kicks the user to the login screen if there is nobody
    // logged in. returns false if user is not logged in.
    public boolean checkLogin(){
        if(!sharedPreferences.getBoolean(IS_LOGGED_IN, false)){
            sendUserToLoginActivity();
            return false;
        }
        return true;
    }

    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();
        sendUserToLoginActivity();
    }

    private void sendUserToLoginActivity() {
        Intent i = new Intent(context, LoginActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // Staring Login Activity
        context.startActivity(i);
    }
}
