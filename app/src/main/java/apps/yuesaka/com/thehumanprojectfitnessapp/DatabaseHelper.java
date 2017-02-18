package apps.yuesaka.com.thehumanprojectfitnessapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * A Singleton implementation of the database helper.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static DatabaseHelper dbInstance;

    private static final String DATABASE_NAME = "FitnessAppDatabase";
    private static final int DATABASE_VERSION = 1;

    private static final String USER_INFO_TABLE_NAME = "user_info";
    private static final String KEY_USER_INFO_ID = "id";
    private static final String KEY_USER_INFO_USERNAME = "username";
    private static final String KEY_USER_INFO_PASSWORD = "password";
    private static final String KEY_USER_INFO_STEPS_TODAY = "steps_today";
    private static final String KEY_USER_INFO_NUM_MILETONES = "num_milestones";
    private static final String KEY_USER_INFO_HEIGHT_CM = "height_cm";
    private static final String KEY_USER_INFO_SEX = "sex";

    private static final String USER_INFO_TABLE_CREATE =
            "CREATE TABLE " + USER_INFO_TABLE_NAME + " (" +
                    KEY_USER_INFO_ID + " INTEGER PRIMARY KEY, " +
                    KEY_USER_INFO_USERNAME + " TEXT, " +
                    KEY_USER_INFO_PASSWORD + " TEXT, " +
                    KEY_USER_INFO_STEPS_TODAY + " INTEGER, " +
                    KEY_USER_INFO_NUM_MILETONES + " INTEGER, " +
                    KEY_USER_INFO_HEIGHT_CM + " INTEGER, " +
                    KEY_USER_INFO_SEX + " TEXT);";

    private static final String STEP_LOG_TABLE_NAME = "step_log";
    private static final String KEY_STEP_LOG_ID = "id";
    private static final String KEY_STEP_LOG_USERNAME = "username";
    private static final String KEY_STEP_LOG_STEPS = "steps";
    private static final String KEY_STEP_LOG_DATE = "date";

    private static final String STEP_LOG_TABLE_CREATE =
            "CREATE TABLE " + STEP_LOG_TABLE_NAME + " (" +
                    KEY_STEP_LOG_ID + " INTEGER PRIMARY KEY, " +
                    KEY_STEP_LOG_USERNAME + " TEXT, " +
                    KEY_STEP_LOG_STEPS + " INTEGER, " +
                    KEY_STEP_LOG_DATE + " TEXT);";

    public static synchronized DatabaseHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (dbInstance == null) {
            dbInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return dbInstance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(USER_INFO_TABLE_CREATE);
        db.execSQL(STEP_LOG_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + USER_INFO_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + STEP_LOG_TABLE_NAME);
        onCreate(db);
    }

    public long insertUser(String username, String password, int height, String sex) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_USER_INFO_USERNAME, username);
        contentValues.put(KEY_USER_INFO_PASSWORD, password);
        contentValues.put(KEY_USER_INFO_STEPS_TODAY, 0);
        contentValues.put(KEY_USER_INFO_NUM_MILETONES, 0);
        contentValues.put(KEY_USER_INFO_HEIGHT_CM, height);
        contentValues.put(KEY_USER_INFO_SEX, sex);
        return db.insert(USER_INFO_TABLE_NAME, null, contentValues);
    }

    public int getUserId(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =  db.rawQuery("select * from " + USER_INFO_TABLE_NAME + " where " +
                KEY_USER_INFO_USERNAME + "= '" + username + "'", null);
        String userId = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    userId = cursor.getString(cursor.getColumnIndex(KEY_USER_INFO_ID));
                } while (cursor.moveToNext());
            }
        }
        return Integer.valueOf(userId);
    }

    public boolean usernameExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select * from " + USER_INFO_TABLE_NAME + " where " +
                KEY_USER_INFO_USERNAME + "= '" + username +"'", null);
        return res.getCount() > 0;
    }

    public boolean isCorrectPassword(String username, String enterdPassword) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =  db.rawQuery("select * from " + USER_INFO_TABLE_NAME + " where " +
                KEY_USER_INFO_USERNAME + "= '" + username + "'", null);
        String dbPassword = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    dbPassword = cursor.getString(cursor.getColumnIndex(KEY_USER_INFO_PASSWORD));
                } while (cursor.moveToNext());
            }
        }
        return enterdPassword.equals(dbPassword);
    }

    public void updateStepsToday(long id, int stepIncrement) {
        SQLiteDatabase db = this.getWritableDatabase();
        // New value for one column
        ContentValues values = new ContentValues();
        int newStepValue = getStepsToday(id) + stepIncrement;
        values.put(KEY_USER_INFO_STEPS_TODAY, newStepValue);
        String selection = KEY_USER_INFO_ID + " LIKE ?";
        String[] selectionArgs = {Long.toString(id)};
        int count = db.update(
                USER_INFO_TABLE_NAME,
                values,
                selection,
                selectionArgs);
    }

    public int getStepsToday(long id) {
        int numStepsToday = 0;
        Cursor cursor = getUserInfo((int) id);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    numStepsToday =
                            cursor.getInt(cursor.getColumnIndex(KEY_USER_INFO_STEPS_TODAY));
                } while (cursor.moveToNext());
            }
        }
        return numStepsToday;
    }

    public Cursor getUserInfo(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select * from " + USER_INFO_TABLE_NAME + " where " + KEY_STEP_LOG_ID + "=" +
                id + "", null);
        return res;
    }


    /**
     * DEBUGGING PURPOSES
     */
    // This is for debugging purposes.
    public ArrayList<Cursor> getData(String Query){
        //get writable database
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        String[] columns = new String[] { "mesage" };
        //an array list of cursor to save two cursors one has results from the query
        //other cursor stores error message if any errors are triggered
        ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
        MatrixCursor Cursor2= new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);


        try{
            String maxQuery = Query ;
            //execute the query results will be save in Cursor c
            Cursor c = sqlDB.rawQuery(maxQuery, null);


            //add value to cursor2
            Cursor2.addRow(new Object[] { "Success" });

            alc.set(1,Cursor2);
            if (null != c && c.getCount() > 0) {


                alc.set(0,c);
                c.moveToFirst();

                return alc ;
            }
            return alc;
        } catch(SQLException sqlEx){
            Log.d("printing exception", sqlEx.getMessage());
            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+sqlEx.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        } catch(Exception ex){

            Log.d("printing exception", ex.getMessage());

            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+ex.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        }


    }
}
