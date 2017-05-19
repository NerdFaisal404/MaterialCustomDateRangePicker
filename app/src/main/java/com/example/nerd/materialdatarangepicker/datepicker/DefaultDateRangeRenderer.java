package com.example.nerd.materialdatarangepicker.datepicker;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;

import com.example.nerd.materialdatarangepicker.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Creado por jcvallejo en 14/11/16.
 */
public class DefaultDateRangeRenderer implements DateRangeView.DateRangeRenderer {
    private static final SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("d", Locale.getDefault());

    private final TextPaint monthDayPaint;
    private final Paint selectionPaint;
    private final Paint selectionPaintDraw;
    private final float textHeight;
    private final int unselectedTextColor;
    private final int selectedTextColor;
    private final RectF rectf;
    private final int eventColor;
    private final TextPaint weekDayLabelPaint;

    @SuppressWarnings("ResourceType")
    public DefaultDateRangeRenderer(Context context) {
        int weekDayLabelTextColor = ContextCompat.getColorStateList(context, android.R.color.primary_text_light).getDefaultColor();
        float textSize = context.getResources().getDimension(R.dimen.weekday_text_size);

        TypedArray a = context.getTheme().obtainStyledAttributes(new int[]{
                R.attr.colorAccent,
                R.attr.colorPrimary,
                android.R.attr.textColorPrimary,
                android.R.attr.textColorPrimaryInverse,
                android.R.attr.textColorSecondary,
        });
        int selectionColor, selectionDrawColor;
        try {
            selectionColor = a.getColor(0, 0);
            selectionDrawColor = 0xffc3fdff;
            eventColor = 0xffC5E1A5; //a.getColor(1, 0);
            unselectedTextColor = a.getColor(2, 0);
            selectedTextColor = a.getColor(3, 0);
            weekDayLabelTextColor = a.getColor(4, 0);
        } finally {
            a.recycle();
        }


        monthDayPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        monthDayPaint.setTextAlign(Paint.Align.CENTER);
        monthDayPaint.setTextSize(textSize);
        monthDayPaint.setColor(selectedTextColor);

        weekDayLabelPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        weekDayLabelPaint.setTextSize(textSize);
        weekDayLabelPaint.setColor(weekDayLabelTextColor);
        weekDayLabelPaint.setTextAlign(Paint.Align.CENTER);

        this.selectionPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        selectionPaint.setColor(selectionColor);

        this.selectionPaintDraw = new Paint(Paint.ANTI_ALIAS_FLAG);
        selectionPaintDraw.setColor(selectionDrawColor);

        Paint.FontMetrics metrics = monthDayPaint.getFontMetrics();
        textHeight = (metrics.bottom + metrics.top) / 2;

        Calendar from = Calendar.getInstance();
        from.set(Calendar.DAY_OF_MONTH, 25);
        Calendar to = Calendar.getInstance();
        to.add(Calendar.MONTH, 1);
        to.set(Calendar.DAY_OF_MONTH, 5);

        rectf = new RectF();
    }

    @Override
    public void drawMonthDay(Canvas canvas, MonthView.CellInfo cellInfo, DateRange dateRange) {
        boolean isInRange = dateRange.isInRange(cellInfo.day);
        boolean hasEvents = Math.random() > 0.5;
        float radius = Math.min(cellInfo.cell.width(), cellInfo.cell.height()) / 2;
        float x = cellInfo.cell.centerX();
        float y = cellInfo.cell.centerY();
        boolean belongsToMonth = CalendarUtils.belongsToMonth(cellInfo.day, cellInfo.currentMonth);

        float topOffset = cellInfo.cell.height() - radius * 2;
        float leftOffset = cellInfo.cell.width() - radius * 2;
        if (isInRange) {
            boolean isFrom = CalendarUtils.isSameDay(dateRange.getFrom(), cellInfo.day);
            boolean isTo = CalendarUtils.isSameDay(dateRange.getTo(), cellInfo.day);
            if (!isFrom || !isTo) {
                if (isFrom) {
                    rectf.set(x, 0, cellInfo.cell.right, radius * 2);
                } else if (isTo) {
                    rectf.set(cellInfo.cell.left, 0, x, radius * 2);
                } else {
                    rectf.set(cellInfo.cell.left, 0, cellInfo.cell.right, radius * 2);
                }
                rectf.offset(0, cellInfo.cell.top + topOffset);
                canvas.drawRect(rectf, selectionPaintDraw);
            }
            if (isFrom || isTo) {
                rectf.set(0, 0, radius * 2, radius * 2);
                rectf.offset(cellInfo.cell.left + leftOffset / 2, cellInfo.cell.top + topOffset);
                canvas.drawOval(rectf, selectionPaint);
               // CommonUtility.IS_DATE_SELECTED = true;
            }
            //monthDayPaint.setColor(selectedTextColor);
            monthDayPaint.setFakeBoldText(true);
        }
        else {
            //monthDayPaint.setColor(unselectedTextColor);
            monthDayPaint.setFakeBoldText(false);
        }


        if (belongsToMonth) {
           /* if (hasEvents) {
                float sradius = radius * 1.4f;
                topOffset = cellInfo.cell.height() - sradius;
                leftOffset = cellInfo.cell.width() - sradius;

                rectf.set(0, 0, sradius, sradius);
                rectf.offset(cellInfo.cell.left + leftOffset / 2, cellInfo.cell.top + topOffset / 2);
                monthDayPaint.setColor(isInRange ? 0x77ffffff : eventColor);
                canvas.drawOval(rectf, monthDayPaint);
            }*/

            //monthDayPaint.setTypeface(hasEvents ? Typeface.DEFAULT_BOLD : Typeface.DEFAULT);
            monthDayPaint.setColor(unselectedTextColor);
            canvas.drawText(DAY_FORMAT.format(cellInfo.day.getTime()), x, y - textHeight, monthDayPaint);
        }
    }

    @Override
    public void drawWeekDay(Canvas canvas, MonthView.CellInfo cellInfo) {
        Locale locale = Locale.getDefault();
        String localWeekDisplayName = cellInfo.day.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, locale);
        String weekString = localWeekDisplayName.toUpperCase(locale).substring(0, 1);

        if (locale.equals(Locale.CHINA) || locale.equals(Locale.CHINESE) || locale.equals(Locale.SIMPLIFIED_CHINESE) || locale.equals(Locale.TRADITIONAL_CHINESE)) {
            int len = localWeekDisplayName.length();
            weekString = localWeekDisplayName.substring(len -1, len);
        }

        if (locale.getLanguage().equals("he") || locale.getLanguage().equals("iw")) {
            if(cellInfo.day.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY) {
                int len = localWeekDisplayName.length();
                weekString = localWeekDisplayName.substring(len - 2, len - 1);
            }
            else {
                // I know this is duplication, but it makes the code easier to grok by
                // having all hebrew code in the same block
                weekString = localWeekDisplayName.toUpperCase(locale).substring(0, 1);
            }
        }
        canvas.drawText(weekString, cellInfo.cell.centerX(), cellInfo.cell.centerY() - textHeight, weekDayLabelPaint);

    }
}