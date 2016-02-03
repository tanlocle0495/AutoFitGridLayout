package com.liuzhuang.autofitgridlayout.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.liuzhuang.acgridlayout.AnimateCalendarGridLayout;
import com.liuzhuang.autofitgridlayout.R;
import com.liuzhuang.autofitgridlayout.adapters.CalendarAdapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by GIGAMOLE on 03.02.2016.
 */
public class CalendarActivity extends AppCompatActivity implements View.OnClickListener {

    private View mBtnPrev;
    private View mBtnNext;
    private TextView mTxtDate;

    private Calendar mCalendar;
    private AnimateCalendarGridLayout mCalendarView;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        initUI();
        setUI();
    }

    private void initUI() {
        mBtnPrev = findViewById(R.id.btn_calendar_prev);
        mBtnNext = findViewById(R.id.btn_calendar_next);
        mTxtDate = (TextView) findViewById(R.id.txt_calendar_date);
        mCalendarView = (AnimateCalendarGridLayout) findViewById(R.id.acgl);
    }

    private void setUI() {
        mCalendar = Calendar.getInstance();
        refreshDate();

        mBtnNext.setOnClickListener(this);
        mBtnPrev.setOnClickListener(this);

        final CalendarAdapter calendarAdapter = new CalendarAdapter(getBaseContext());
        mCalendarView.setCalendarAdapter(calendarAdapter, mCalendar);
        mCalendarView.setOnChangeListener(new AnimateCalendarGridLayout.OnChangeListener() {
            @Override
            public void onChangeStart() {
                toggleCalendar(false);
            }

            @Override
            public void onChangeFinish() {
                toggleCalendar(true);
            }
        });
        mCalendarView.setOnDateSelectedListener(new AnimateCalendarGridLayout.OnDateSelectedListener() {
            @Override
            public void onDateSelect(final Calendar calendar) {
                mCalendar = calendar;
                refreshDate();
            }
        });
    }

    private void refreshDate() {
        mTxtDate.setText(
                new SimpleDateFormat("d MMMM yyyy").format(mCalendar.getTime()).toUpperCase()
        );
    }

    private void toggleCalendar(final boolean isEnabled) {
        mCalendarView.setEnabled(isEnabled);
        mBtnNext.setEnabled(isEnabled);
        mBtnPrev.setEnabled(isEnabled);
    }


    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.btn_calendar_prev:
                mCalendar.add(Calendar.MONTH, -1);
                break;
            case R.id.btn_calendar_next:
                mCalendar.add(Calendar.MONTH, 1);
                break;
        }

        refreshDate();
        mCalendarView.setDate(mCalendar);
    }
}
