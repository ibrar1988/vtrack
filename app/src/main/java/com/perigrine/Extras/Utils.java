package com.perigrine.Extras;

import android.content.Context;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.widget.Toast;

/**
 * Created by ragamai on 12/9/17.
 */
public class Utils {

    public static boolean isValidEmail(CharSequence emailId) {
        return !TextUtils.isEmpty(emailId) && android.util.Patterns.EMAIL_ADDRESS.matcher(emailId).matches();
    }

    public static void showToast(Context context, String message){
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
    }

    public static String convertToDate(long dateInMilliseconds) {
        String dateFormat = "MMM dd, yyyy HH:mm";
        return DateFormat.format(dateFormat, dateInMilliseconds).toString();
    }
}
