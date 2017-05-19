package com.example.nerd.materialdaterangepicker.datepicker;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.nerd.materialdaterangepicker.R;

import java.util.Calendar;


public class MonthView extends View {
    private static final int DEFAULT_WEEK_DAY_COUNT = 7;
    private static final int DEFAULT_ROW_HEIGHT = 35;
    private static final int DEFAULT_NUM_ROWS = 6;

    private MonthViewListener delegate;
    private MonthViewRenderer renderer;

    private int width;
    private int weekDayCount = DEFAULT_WEEK_DAY_COUNT;
    private int rowHeight;
    private int numRows;
    private Calendar calendar;
    private Calendar reusableCalendar;
    private float dayVerticalPadding;
    private CellInfo cellInfo;

    public MonthView(Context context) {
        this(context, null, 0);
    }

    public MonthView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MonthView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init(context, attrs, defStyleAttr, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MonthView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        rowHeight = (int) (DEFAULT_ROW_HEIGHT * context.getResources().getDisplayMetrics().density + 0.5);
        numRows = DEFAULT_NUM_ROWS;
        dayVerticalPadding = context.getResources().getDimension(R.dimen.day_vertical_padding);

        this.calendar = Calendar.getInstance();
        this.reusableCalendar = Calendar.getInstance();
        this.cellInfo = new CellInfo();

        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.MonthView,
                    defStyleAttr, defStyleRes);

            try {
                rowHeight = (int) a.getDimension(R.styleable.MonthView_drp_rowHeight, rowHeight);
            } finally {
                a.recycle();
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.width = w;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), rowHeight * (1 + numRows) + getPaddingBottom() + getPaddingTop());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawWeekdayLabels(canvas);
        drawDays(canvas);
    }

    private void drawWeekdayLabels(Canvas canvas) {
        int paddingStart = ViewCompat.getPaddingStart(this);
        int paddingEnd = ViewCompat.getPaddingEnd(this);
        int paddingTop = getPaddingTop();
        int dayWidth = (this.width - paddingStart - paddingEnd) / weekDayCount;

        int weekStartDay = reusableCalendar.getFirstDayOfWeek();
        for (int i = 0; i < weekDayCount; i++) {
            int calendarDay = (i + weekStartDay) % weekDayCount;
            cellInfo.day.set(Calendar.DAY_OF_WEEK, calendarDay);
            cellInfo.cell.set(0, 0, dayWidth, rowHeight);
            cellInfo.cell.offset(paddingStart + i * dayWidth, paddingTop);
            renderer.drawWeekDay(canvas, cellInfo);
        }
    }

    private void drawDays(Canvas canvas) {
        if (delegate == null) {
            return;
        }

        float paddingStart = ViewCompat.getPaddingStart(this);
        float paddingEnd = ViewCompat.getPaddingEnd(this);
        float dayWidthHalf = (this.width - paddingStart - paddingEnd) / (weekDayCount * 2);
        float y = rowHeight * 1.5f;

        cellInfo.currentMonth.setTimeInMillis(calendar.getTimeInMillis());
        reusableCalendarToFirstCellDay(cellInfo.day);

        int numCells = weekDayCount * numRows;
        for (int i = 0; i < numCells; ++i) {
            float x = ((i % weekDayCount) * 2 + 1) * dayWidthHalf + paddingStart;

            cellInfo.cell.set(x - dayWidthHalf, y - rowHeight / 2 + dayVerticalPadding, x + dayWidthHalf, y + rowHeight / 2 - dayVerticalPadding);
            renderer.drawMonthDay(canvas, cellInfo);
            if (i % weekDayCount == weekDayCount - 1) {
                y += rowHeight;
            }
            cellInfo.day.add(Calendar.DATE, 1);
        }
    }

    private void reusableCalendarToFirstCellDay(Calendar reusableCalendar) {
        reusableCalendar.setTimeInMillis(calendar.getTimeInMillis());
        reusableCalendar.set(Calendar.DAY_OF_MONTH, 1);
        int weekdayOfFirstDay = reusableCalendar.get(Calendar.DAY_OF_WEEK);
        int firstDayOfWeek = reusableCalendar.getFirstDayOfWeek();
        int offset = (weekdayOfFirstDay - firstDayOfWeek + weekDayCount) % 7;
        reusableCalendar.add(Calendar.DATE, -offset);
    }

    public void setDelegate(MonthViewListener delegate) {
        this.delegate = delegate;
    }

    public void setRenderer(MonthViewRenderer renderer) {
        this.renderer = renderer;
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (delegate != null && event.getAction() == MotionEvent.ACTION_UP) {
            float paddingStart = ViewCompat.getPaddingStart(this);
            float x = event.getX() - paddingStart;
            float y = event.getY() - rowHeight - getPaddingTop();

            if (y >= 0) {
                reusableCalendarToFirstCellDay(reusableCalendar);

                float paddingEnd = ViewCompat.getPaddingEnd(this);
                float dayWidth = (this.width - paddingStart - paddingEnd) / weekDayCount;

                int row = (int) (y / rowHeight);
                int col = (int) (x / dayWidth);
                int cell = row * weekDayCount + col;

                Calendar cal = Calendar.getInstance();
                reusableCalendar.add(Calendar.DATE, cell);

                int currentDayOfMonth = cal.get(Calendar.DAY_OF_MONTH);

                Log.e("Calerder_Aziz","Current ="+cal.getTimeInMillis()+" Select ="+reusableCalendar.getTimeInMillis()+" ="+currentDayOfMonth+", "+cell);

                if(cal.getTimeInMillis() > reusableCalendar.getTimeInMillis()){
                    if(currentDayOfMonth == cell+1){
                       //Do nothing
                    }else{
                        return false;
                    }
                }
                delegate.onDayClicked((Calendar) reusableCalendar.clone());
                return true;
            }
        }

        return true;
    }

    public void setDate(int year, int month, int day) {
        calendar.set(year, month, day);
        invalidate();
    }

    public static class CellInfo {
        public RectF cell;
        public Calendar currentMonth;
        public Calendar day;

        CellInfo() {
            this.cell = new RectF();
            this.currentMonth = Calendar.getInstance();
            this.day = Calendar.getInstance();
        }
    }

    public interface MonthViewListener {
        void onDayClicked(Calendar calendar);
    }

    public interface MonthViewRenderer {
        void drawMonthDay(Canvas canvas, CellInfo cellInfo);
        void drawWeekDay(Canvas canvas, CellInfo cellInfo);
    }
}
