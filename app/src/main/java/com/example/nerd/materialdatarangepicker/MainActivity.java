package com.example.nerd.materialdatarangepicker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.nerd.materialdatarangepicker.datepicker.CalendarUtils;
import com.example.nerd.materialdatarangepicker.datepicker.DateRange;
import com.example.nerd.materialdatarangepicker.datepicker.DateRangePickerDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity {
    private TextView checkInOutview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkInOutview = (TextView) findViewById(R.id.checkInOutView);
        checkInOutview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDateRangePicker();
            }
        });
    }

    private void openDateRangePicker() {
        DateRange dateRange = null;
        if (!TextUtils.isEmpty(checkInOutview.getText().toString())) {

            SimpleDateFormat formatter = new SimpleDateFormat("MMM d, yyyy");
            Date dateStart = null, dateEnd = null;

            String fullText = checkInOutview.getText().toString();
            String[] splitsValue = fullText.split("/");
            String start = splitsValue[0];
            String end = splitsValue[1];
            try {
                dateStart = formatter.parse(start);

                dateEnd = formatter.parse(end);

            } catch (ParseException e) {
                e.printStackTrace();
            }
            Calendar form = new GregorianCalendar();
            form.setTime(dateStart);

            Calendar to = new GregorianCalendar();
            to.setTime(dateEnd);

            dateRange = new DateRange();
            dateRange.setFrom(form);
            dateRange.setTo(to);
        }

        new DateRangePickerDialog.Builder(MainActivity.this)
                .initialDateRange(dateRange != null ? dateRange : DateRange.today())
                .onDateRangeSelected(new DateRangePickerDialog.OnDateRangeSelectedListener() {
                    @Override
                    public void onDateRangeSelected(MaterialDialog dialog, DateRange dateRange) {
                        boolean isSameDate = CalendarUtils.isSameDay(dateRange.getFrom(), dateRange.getTo());
                        if (!isSameDate) {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy");
                            String fullDate = dateFormat.format(dateRange.getFrom().getTime()) + "/" +
                                    dateFormat.format(dateRange.getTo().getTime());
                            checkInOutview.setText(fullDate);

                        } else {
                            checkInOutview.setText("");
                        }
                    }
                }).show();
    }


}
