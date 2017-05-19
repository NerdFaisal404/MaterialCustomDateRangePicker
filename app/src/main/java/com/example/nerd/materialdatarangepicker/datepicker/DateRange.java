package com.example.nerd.materialdatarangepicker.datepicker;

import java.util.Calendar;

public class DateRange {
    private Calendar from;
    private Calendar to;

    public DateRange() {
        this(Calendar.getInstance(), Calendar.getInstance());
    }

    public DateRange(Calendar from, Calendar to) {
        this.setFrom(from);
        this.setTo(to);
    }

    public static DateRange today() {
        return new DateRange();
    }

    public static DateRange yesterday () {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return new DateRange(cal, cal);
    }

    public static DateRange tomorrow () {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 1);
        return new DateRange(cal, cal);
    }

    public static DateRange nextWeekend() {
        DateRange range = new DateRange();
        Calendar cal = Calendar.getInstance();

        cal.add(Calendar.DATE, Calendar.FRIDAY - cal.get(Calendar.DAY_OF_WEEK));
        range.setFrom(cal);

        cal.add(Calendar.DATE, 2);
        range.setTo(cal);

        return range;
    }

    public static DateRange currentWeek() {
        DateRange range = new DateRange();
        Calendar cal = Calendar.getInstance();

        int daysToMonday = cal.getFirstDayOfWeek() - cal.get(Calendar.DAY_OF_WEEK);
        cal.add(Calendar.DATE, daysToMonday);
        range.setFrom(cal);
        cal.add(Calendar.DATE, 6);
        range.setTo(cal);

        return range;
    }

    public static DateRange nextWeek() {
        DateRange range = new DateRange();
        Calendar cal = Calendar.getInstance();

        int daysToMonday = cal.getFirstDayOfWeek() - cal.get(Calendar.DAY_OF_WEEK);
        daysToMonday = daysToMonday > 0 ? daysToMonday : 7 + daysToMonday;
        cal.add(Calendar.DATE, daysToMonday);
        range.setFrom(cal);
        cal.add(Calendar.DATE, 6);
        range.setTo(cal);

        return range;
    }

    public static DateRange currentMonth() {
        DateRange range = new DateRange();
        Calendar cal = Calendar.getInstance();

        cal.set(Calendar.DAY_OF_MONTH, 1);
        range.setFrom(cal);
        cal.add(Calendar.MONTH, 1);
        cal.add(Calendar.DATE, -1);
        range.setTo(cal);

        return range;
    }

    public static DateRange nextMonth() {
        DateRange range = new DateRange();
        Calendar cal = Calendar.getInstance();

        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.add(Calendar.MONTH, 1);
        range.setFrom(cal);
        cal.add(Calendar.MONTH, 1);
        cal.add(Calendar.DATE, -1);
        range.setTo(cal);

        return range;
    }

    public static DateRange nextYear() {
        DateRange range = new DateRange();
        Calendar cal = Calendar.getInstance();

        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.MONTH, 1);
        cal.add(Calendar.YEAR, 1);
        range.setFrom(cal);
        cal.add(Calendar.YEAR, 1);
        cal.add(Calendar.DATE, -1);
        range.setTo(cal);

        return range;
    }

    public static DateRange currentYear() {
        DateRange range = new DateRange();
        Calendar cal = Calendar.getInstance();

        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.MONTH, 1);
        range.setFrom(cal);
        cal.add(Calendar.YEAR, 1);
        cal.add(Calendar.DATE, -1);
        range.setTo(cal);

        return range;
    }

    public void setFrom(Calendar from) {
        if (from == null) {
            from = to;
        }
        this.from = Calendar.getInstance();
        this.from.set(from.get(Calendar.YEAR), from.get(Calendar.MONTH), from.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
    }

    public void setTo(Calendar to) {
        if (to == null) {
            to = from;
        }
        this.to = Calendar.getInstance();
        this.to.set(to.get(Calendar.YEAR), to.get(Calendar.MONTH), to.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
    }


    public boolean isInRange(Calendar day) {
        return !day.before(from) && !day.after(to);
    }

    public Calendar getFrom() {
        return from;
    }

    public Calendar getTo() {
        return to;
    }
}
