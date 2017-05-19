package com.example.nerd.materialdaterangepicker.datepicker;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

import java.util.Calendar;


public class DateRangeView extends ViewPager implements MonthView.MonthViewListener, MonthView.MonthViewRenderer {
    public static final int DEFAULT_MONTHS_BEFORE = 0;
    public static final int DEFAULT_MONTHS_AFTER = 24;

    private final MonthViewPagerAdapter adapter;
    private DateRange dateRange;

    private DateRangeRenderer renderer;
    private DateRangeMutator mutator;
    private DateRangeViewListener dateRangeViewListener;

    public DateRangeView(Context context) {
        this(context, null);
    }

    public DateRangeView(Context context, AttributeSet attrs) {
        super(context, attrs);

        dateRange = new DateRange();
        mutator = new DefaultDateRangeMutator();

        adapter = new MonthViewPagerAdapter(context, DEFAULT_MONTHS_BEFORE, DEFAULT_MONTHS_AFTER);
        adapter.setListener(this);
        adapter.setRenderer(this);
        setCurrentItem(0);
        this.setAdapter(adapter);
        this.addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

            @Override
            public void onPageSelected(int position) {
                if (dateRangeViewListener != null) {
                    Calendar calendarMonth = adapter.getMonthCalendarForPosition(position);
                    dateRangeViewListener.onCurrentMonthChanged(DateRangeView.this, calendarMonth);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) { }
        });
    }

    protected void invalidateMonthViews() {
        adapter.notifyDataSetChanged();
        for (int i = 0; i < this.getChildCount(); ++i) {
            View view = getChildAt(i);
            if (view instanceof  MonthView) {
                view.invalidate();
            }
        }
    }

  /*  @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = 0;
        for (int i = 0; i < getChildCount(); ++i) {
            View child = getChildAt(i);
            if (child != null && child instanceof MonthView) {
                child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                height = Math.max(child.getMeasuredHeight(), height);
            }
        }

        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }*/


    @Override
    public void onDayClicked(Calendar day) {
        mutator.mutateRangeWithDay(dateRange, day);
        invalidateMonthViews();

        if (dateRangeViewListener != null) {
            dateRangeViewListener.onDateRangeChanged(this, dateRange);
        }
    }

    @Override
    public void drawMonthDay(Canvas canvas, MonthView.CellInfo cellInfo) {
        if (renderer == null) {
            renderer = new DefaultDateRangeRenderer(getContext());
        }
        renderer.drawMonthDay(canvas, cellInfo, dateRange);
    }

    @Override
    public void drawWeekDay(Canvas canvas, MonthView.CellInfo cellInfo) {
        if (renderer == null) {
            renderer = new DefaultDateRangeRenderer(getContext());
        }
        renderer.drawWeekDay(canvas, cellInfo);
    }

    public void nextMonth() {
        int item = Math.min(getCurrentItem() + 1, adapter.getCount() - 1);
        this.setCurrentItem(item, true);
    }

    public void previousMonth() {
        int item = Math.max(getCurrentItem() - 1, 0);
        this.setCurrentItem(item, true);
    }

    public void setOnMonthChangeListener(DateRangeViewListener dateRangeViewListener) {
        this.dateRangeViewListener = dateRangeViewListener;
    }

    public Calendar getCurrentMonth() {
        return adapter.getMonthCalendarForPosition(getCurrentItem());
    }


    public interface DateRangeRenderer {
        void drawMonthDay(Canvas canvas, MonthView.CellInfo cellInfo, DateRange dateRange);
        void drawWeekDay(Canvas canvas, MonthView.CellInfo cellInfo);
    }

    public interface DateRangeMutator {
        void mutateRangeWithDay(DateRange dateRange, Calendar day);
    }

    public DateRange getDateRange() {
        return dateRange;
    }

    public void setDateRange(DateRange dateRange) {
        this.dateRange = dateRange;
        invalidateMonthViews();
    }

    interface DateRangeViewListener {
        void onCurrentMonthChanged(DateRangeView dateRangeView, Calendar calendarMonth);
        void onDateRangeChanged(DateRangeView dateRangeView, DateRange dateRange);
    }
}
