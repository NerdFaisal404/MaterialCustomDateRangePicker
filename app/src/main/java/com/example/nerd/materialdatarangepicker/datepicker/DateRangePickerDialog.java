package com.example.nerd.materialdatarangepicker.datepicker;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.nerd.materialdatarangepicker.R;
import com.example.nerd.materialdatarangepicker.util.DateUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class DateRangePickerDialog extends MaterialDialog implements DateRangeView.DateRangeViewListener {
    private static final SimpleDateFormat CALENDAR_HEADER_DATE_FORMAT = new SimpleDateFormat("MMMM y", Locale.getDefault());
    private static final SimpleDateFormat DAY_DATE_FORMAT = new SimpleDateFormat("EEEE", Locale.getDefault());
    private static final SimpleDateFormat MONTH_DATE_FORMAT = new SimpleDateFormat("MMM d", Locale.getDefault());
    private static final SimpleDateFormat YEAR_DATE_FORMAT = new SimpleDateFormat("yyyy", Locale.getDefault());

    private static TextView checkInTv,checkOutTv, nightCountTv;

    private final View calendarFrame;
    private final DateRangeView calendarRange;
    private final TextView calendarHeader;
    private DateRange dateRange;
    private static Context mContext;
    private static ImageView ivSelectedDate;
    public DateRangePickerDialog(Builder builder) {
        super(builder);

        //fromDay = (TextView) this.getCustomView().findViewById(R.id.selection_from_day);
        //fromMonth = (TextView) this.getCustomView().findViewById(R.id.selection_from_month);
        //fromYear = (TextView) this.getCustomView().findViewById(R.id.selection_from_year);
        //toDay = (TextView) this.getCustomView().findViewById(R.id.selection_to_day);
        //toMonth = (TextView) this.getCustomView().findViewById(R.id.selection_to_month);
        //toYear = (TextView) this.getCustomView().findViewById(R.id.selection_to_year);
        checkInTv = (TextView)this.getCustomView().findViewById(R.id.check_in_tv);
        checkOutTv = (TextView)this.getCustomView().findViewById(R.id.check_out_tv);
        nightCountTv = (TextView)this.getCustomView().findViewById(R.id.nightCount_textView);
        ivSelectedDate = (ImageView) this.getCustomView().findViewById(R.id.selectUnselect_iv);

        calendarFrame = this.getCustomView().findViewById(R.id.calendar_frame);
        calendarRange = (DateRangeView) calendarFrame.findViewById(R.id.calendar_range);
        calendarHeader = (TextView) calendarFrame.findViewById(R.id.calendar_header);
        calendarRange.setOnMonthChangeListener(this);
        calendarFrame.findViewById(R.id.calendar_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendarRange.nextMonth();
            }
        });
        calendarFrame.findViewById(R.id.calendar_prev).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendarRange.previousMonth();
            }
        });


        this.dateRange = builder.dateRange;
        calendarRange.setDateRange(builder.dateRange);
        onCurrentMonthChanged(calendarRange, calendarRange.getCurrentMonth());
        updateHeader();
    }

    private void updateHeader() {
        Date from = dateRange.getFrom().getTime();
        Date to = dateRange.getTo().getTime();
        boolean isSameDate = CalendarUtils.isSameDay(dateRange.getFrom(),dateRange.getTo());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        String start = dateFormat.format(from);
        String end = dateFormat.format(to);
        long dayCount = DateUtil.printDifference(start, end);

        Log.e("Date_format","Form ="+from.toString()+" to ="+to.toString());
        String checkIn = DAY_DATE_FORMAT.format(from)
                +", "+MONTH_DATE_FORMAT.format(from)
                +", "+YEAR_DATE_FORMAT.format(from);
        String checkOut = DAY_DATE_FORMAT.format(to)
                +", "+MONTH_DATE_FORMAT.format(to)
                +", "+YEAR_DATE_FORMAT.format(to);

        /*this.fromDay.setText(DAY_DATE_FORMAT.format(from));
        this.fromMonth.setText(MONTH_DATE_FORMAT.format(from).toUpperCase(Locale.getDefault()).replace(".", ""));
        this.fromYear.setText(YEAR_DATE_FORMAT.format(from));

        this.toDay.setText(DAY_DATE_FORMAT.format(to));
        this.toMonth.setText(MONTH_DATE_FORMAT.format(to).toUpperCase(Locale.getDefault()).replace(".", ""));
        this.toYear.setText(YEAR_DATE_FORMAT.format(to));*/

        if (isSameDate){
            ivSelectedDate.setImageDrawable(mContext.getResources().getDrawable(R.drawable.unactive_date_picker));
        }else {
            ivSelectedDate.setImageDrawable(mContext.getResources().getDrawable(R.drawable.active_date_picker));
        }

        checkInTv.setText(checkIn);
        if(!isSameDate) {
            checkOutTv.setText(checkOut);
        }else {
            checkOutTv.setText(mContext.getString(R.string.check_out));
        }
        if(dayCount > 1){
            nightCountTv.setText(String.format(mContext.getString(R.string.nights_count), dayCount));
        }else {
            nightCountTv.setText(String.format(mContext.getString(R.string.night_count), dayCount));
        }
    }

    private static void resetDate(){

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMM d, yyyy");
        String strDate = sdf.format(cal.getTime());
        //System.out.println("Current date in String Format: " + strDate);

        checkInTv.setText(strDate);
        checkOutTv.setText(mContext.getString(R.string.check_out));
        nightCountTv.setText(String.format(mContext.getString(R.string.night_count), 0));
        ivSelectedDate.setImageDrawable(mContext.getResources().getDrawable(R.drawable.unactive_date_picker));
    }

    @Override
    public void onCurrentMonthChanged(DateRangeView view, Calendar calendarMonth) {
        calendarHeader.setText(CALENDAR_HEADER_DATE_FORMAT.format(calendarMonth.getTime()));
    }

    @Override
    public void onDateRangeChanged(DateRangeView dateRangeView, DateRange dateRange) {
        this.dateRange = dateRange;
        this.updateHeader();
    }

    public static class Builder extends MaterialDialog.Builder {

        private OnDateRangeSelectedListener onDateRangeSelected;
        private DateRange dateRange;

        public Builder(@NonNull Context context) {
            super(context);
            mContext = context;
            this.customView(R.layout.dialog_date_range, false);
            this.positiveText(android.R.string.ok);
            this.negativeText(android.R.string.cancel);
            this.neutralText(mContext.getString(R.string.clear));
            this.autoDismiss(false);
            this.onPositive(new SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    DateRangePickerDialog dateRangeDialog = (DateRangePickerDialog) dialog;
                    onDateRangeSelected.onDateRangeSelected(dialog,  dateRangeDialog.calendarRange.getDateRange());
                    dialog.dismiss();
                }
            });

            this.onNegative(new SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    dialog.dismiss();
                }
            });

            this.onNeutral(new SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    DateRangePickerDialog dateRangeDialog = (DateRangePickerDialog) dialog;
                    //initialDateRange(DateRange.today());
                    dateRangeDialog.calendarRange.setDateRange(DateRange.today());
                    //onDateRangeSelected.onDateRangeSelected(dialog, DateRange.today());
                    //dateRangeDialog.clearSelectedIndices(true);
                    resetDate();
                }
            });

            dateRange = new DateRange();
            onDateRangeSelected = new OnDateRangeSelectedListener() {
                @Override
                public void onDateRangeSelected(MaterialDialog dialog, DateRange dateRange) {
                    dialog.dismiss();
                }
            };
        }

        public Builder initialDateRange(@NonNull DateRange dateRange) {
            this.dateRange = dateRange;
            return this;
        }

        public Builder onDateRangeSelected(@NonNull OnDateRangeSelectedListener dateRangeSelected) {
            this.onDateRangeSelected = dateRangeSelected;
            return this;
        }

        @Override
        public MaterialDialog build() {
            return new DateRangePickerDialog(this);
        }
    }


    public interface OnDateRangeSelectedListener {
        void onDateRangeSelected(MaterialDialog dialog, DateRange dateRange);
    }
}
