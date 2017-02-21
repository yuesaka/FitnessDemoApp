package apps.yuesaka.com.thehumanprojectfitnessapp;

import android.content.Context;
import android.widget.EditText;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility class.
 */
public final class Utility {
    // Cannot be instantiated.
    private Utility() {}

    private static final double MALE_STEP_LENGTH_FACTOR = 0.415;
    private static final double FEMALE_STEP_LENGTH_FACTOR = 0.413;
    public static final double METER_TO_FEET_CONVERSION = 3.28084;

    public static boolean isRequiredFieldFilled(Context context, EditText editText) {
        boolean isRequiredFieldFilled = !editText.getText().toString().trim().equals("");
        if (!isRequiredFieldFilled) {
            editText.setError(context.getText(R.string.edit_text_field_required));
        }
        return isRequiredFieldFilled;
    }

    public static double stepsToMeter(int numSteps, int height_cm, boolean isMale) {
        double stepLengthFactor = isMale ?  MALE_STEP_LENGTH_FACTOR : FEMALE_STEP_LENGTH_FACTOR;
        return (double)(numSteps * height_cm * stepLengthFactor) / (double) 100;
    }

    public static String getCurrentDateString() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        String dateString = dateFormat.format(date);
        return dateString;
    }

    // formats a double into a string with two digits after the decimal point.
    public static String formatDouble(double number) {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return decimalFormat.format(number);
    }
}
