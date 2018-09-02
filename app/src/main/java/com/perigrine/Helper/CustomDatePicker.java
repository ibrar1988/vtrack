package com.perigrine.Helper;

import android.app.DatePickerDialog;
import android.content.Context;
import com.perigrine.Interfaces.DateSetCallback;
import java.util.Calendar;

/**
 * Created by Ibrar on 05/10/17.
 */

public class CustomDatePicker implements DatePickerDialog.OnDateSetListener {
    private Context ctx;
    private DateSetCallback dateSetCallback;

    // Constructor
    public CustomDatePicker(Context ctx, DateSetCallback callback){
        this.ctx = ctx;
        this.dateSetCallback = callback;
    }

    // Show calendar as date picker dialog
    public void onCreateDialog(){

        //Get default calendar
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create object of DatePickerDialog
        DatePickerDialog dialog = new DatePickerDialog(ctx, this, year, month, day);
        dialog.setTitle("Visitor Tracking");

        // Set current date to date picker dialog
        dialog.getDatePicker().updateDate(year,month,day);

        // Show dialog on user screen
        dialog.show();
    }

    // It gets call when user set date
    @Override
    public void onDateSet(android.widget.DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        String day = Integer.toString(dayOfMonth);
        String month = Integer.toString(monthOfYear);
        String _year = Integer.toString(year);

        // Check day and make it in form of two digit (DD)
        if(dayOfMonth<10)
            day = "0"+day;

        // Check month and make it in form of two digit (MM)
        if(monthOfYear<10)
            month = "0"+month;

        // Make final date in format of DD-MM-YYYY
        String date = day+"-"+month+"-"+year;

        // Send the date through callback using interface
        dateSetCallback.onDateReady(_year, month, day);
    }
}
