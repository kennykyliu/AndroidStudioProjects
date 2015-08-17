package com.mycompany.personalhealthmanagement;

import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.DatePicker;

import java.util.Calendar;

public class DateSettings implements DatePickerDialog.OnDateSetListener {
    Context context;

    public DateSettings(Context context) {
        this.context = context;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
/*        Toast.makeText(context, "Selected date: " + monthOfYear + " / " +
                        dayOfMonth + " / " + year, Toast.LENGTH_SHORT).show();*/
        Calendar cal = Calendar.getInstance();
        cal.set(year, monthOfYear, dayOfMonth);
        long dateInMillis = cal.getTimeInMillis();
        PersonalInfo.showDate(dateInMillis);
    }
}
