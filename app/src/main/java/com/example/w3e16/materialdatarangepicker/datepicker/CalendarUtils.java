package com.example.w3e16.materialdatarangepicker.datepicker;

import java.util.Calendar;

/**
 * Creado por jcvallejo en 17/11/16.
 */

public class CalendarUtils {
    public static boolean isSameDay(Calendar c1, Calendar c2) {
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                && c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH)
                && c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH);
    }

    public static boolean belongsToMonth(Calendar day, Calendar month) {
        return day.get(Calendar.MONTH) == month.get(Calendar.MONTH);
    }

}
