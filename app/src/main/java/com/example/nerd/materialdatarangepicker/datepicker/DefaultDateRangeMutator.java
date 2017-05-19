package com.example.nerd.materialdatarangepicker.datepicker;

import java.util.Calendar;

/**
 * Creado por jcvallejo en 17/11/16.
 */
public class DefaultDateRangeMutator implements DateRangeView.DateRangeMutator {
    @Override
    public void mutateRangeWithDay(DateRange dateRange, Calendar day) {
        if (CalendarUtils.isSameDay(day, dateRange.getFrom())) {
            dateRange.setTo(dateRange.getFrom());
        }
        else if (CalendarUtils.isSameDay(day, dateRange.getTo())) {
            dateRange.setFrom(dateRange.getTo());
        }
        else {
            long fromDiff = Math.abs(day.getTimeInMillis() - dateRange.getFrom().getTimeInMillis());
            long toDiff = Math.abs(day.getTimeInMillis() - dateRange.getTo().getTimeInMillis());
            if (fromDiff < toDiff) {
                dateRange.setFrom(day);
            }
            else {
                dateRange.setTo(day);
            }
        }
    }
}
