package com.example.miniproject;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
//This class is adapted from internet
public class DateCompare {
    public static int compare(String strDate) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date inputDate = dateFormat.parse(strDate);

            if (inputDate != null) {
                // Get the current date
                Date currentDate = new Date();

                // Remove the time portion
                currentDate = removeTimeFromDate(currentDate);
                inputDate = removeTimeFromDate(inputDate);
                Log.e("===================", "current: " + currentDate.toString() + "  input:" +  inputDate.toString());

                // Compare the dates
                int comparisonResult = inputDate.compareTo(currentDate);

                if (comparisonResult < 0) {
                    System.out.println("The input date is before the current date.");
                    return -1;
                } else if (comparisonResult > 0) {
                    System.out.println("The input date is after the current date.");
                    return 1;
                } else {
                    System.out.println("The input date is equal to the current date.");
                    return 0;
                }
            } else {
                System.out.println("Invalid date format.");
                return -2;
            }
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return -3;
    }

    private static Date removeTimeFromDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
}
