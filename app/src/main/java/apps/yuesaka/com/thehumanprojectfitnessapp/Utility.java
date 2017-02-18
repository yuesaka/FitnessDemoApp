package apps.yuesaka.com.thehumanprojectfitnessapp;

import android.content.Context;
import android.widget.EditText;

/**
 * Utility class.
 */
public final class Utility {
    // Cannot be instantiated.
    private Utility() {}

    public static boolean isRequiredFieldFilled(Context context, EditText editText) {
        boolean isRequiredFieldFilled = !editText.getText().toString().trim().equals("");
        if (!isRequiredFieldFilled) {
            editText.setError(context.getText(R.string.edit_text_field_required));
        }
        return isRequiredFieldFilled;
    }
}
