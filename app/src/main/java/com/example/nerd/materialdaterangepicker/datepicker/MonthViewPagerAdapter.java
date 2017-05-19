package com.example.nerd.materialdaterangepicker.datepicker;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.Queue;

public class MonthViewPagerAdapter extends PagerAdapter {
    private final int monthCount;
    private final int todayMonthIndex;
    private final Context appContext;
    private final Queue<View> recycler;
    private final Calendar today;
    private final Calendar reusable;
    private MonthView.MonthViewListener listener;
    private MonthView.MonthViewRenderer renderer;

    MonthViewPagerAdapter(Context context, int monthsBefore, int monthsAfter) {
        this.appContext = context.getApplicationContext();
        this.monthCount = monthsBefore + monthsAfter;
        this.todayMonthIndex = monthsBefore;
        this.recycler = new LinkedList<>();
        this.today = Calendar.getInstance();
        this.today.set(Calendar.DAY_OF_MONTH, 1);
        this.reusable = Calendar.getInstance();
    }

    void setListener(MonthView.MonthViewListener listener) {
        this.listener = listener;
    }

    void setRenderer(MonthView.MonthViewRenderer renderer) {
        this.renderer = renderer;
        this.notifyDataSetChanged();
    }

    @Override
    public Object instantiateItem(ViewGroup parent, int position) {
        MonthView view = (MonthView) recycler.poll();
        if (view == null) {
            view = new MonthView(appContext);
            view.setDelegate(listener);
            view.setRenderer(renderer);
        }

        reusable.setTimeInMillis(today.getTimeInMillis());
        reusable.add(Calendar.MONTH, position - todayMonthIndex);
        view.setDate(reusable.get(Calendar.YEAR), reusable.get(Calendar.MONTH), 1);
        parent.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object object) {
        View view = (View) object;
        collection.removeView(view);
        recycler.add(view);
    }

    @Override
    public int getCount() {
        return monthCount;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    protected Calendar getMonthCalendarForPosition(int position) {
        Calendar monthCalendar = Calendar.getInstance();
        monthCalendar.add(Calendar.MONTH, position - todayMonthIndex);
        return monthCalendar;
    }
}
