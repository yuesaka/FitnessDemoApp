package apps.yuesaka.com.thehumanprojectfitnessapp;

import android.content.Context;
import android.widget.EditText;

/**
 * Utility class.
 */
public final class Utility {
    // Cannot be instantiated.
    private Utility() {}

    private static final double MALE_STEP_LENGTH_FACTOR = 0.415;
    private static final double FEMALE_STEP_LENGTH_FACTOR = 0.413;


    public static boolean isRequiredFieldFilled(Context context, EditText editText) {
        boolean isRequiredFieldFilled = !editText.getText().toString().trim().equals("");
        if (!isRequiredFieldFilled) {
            editText.setError(context.getText(R.string.edit_text_field_required));
        }
        return isRequiredFieldFilled;
    }

    public static double stepsToFoot(int numSteps, int height_cm, boolean isMale) {
        double stepLengthFactor = isMale ?  MALE_STEP_LENGTH_FACTOR : FEMALE_STEP_LENGTH_FACTOR;
        return (double)(numSteps * height_cm * stepLengthFactor) / (double) 100;
    }
}
