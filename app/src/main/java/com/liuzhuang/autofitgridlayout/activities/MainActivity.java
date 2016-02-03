package com.liuzhuang.autofitgridlayout.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.liuzhuang.autofitgridlayout.R;

/**
 * Created by GIGAMOLE on 03.02.2016.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUI();
    }

    private void setUI() {
        findViewById(R.id.btn_filter).setOnClickListener(this);
        findViewById(R.id.btn_calendar).setOnClickListener(this);
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.btn_filter:
                startActivity(new Intent(this, FilterActivity.class));
                break;
            case R.id.btn_calendar:
                startActivity(new Intent(this, CalendarActivity.class));
                break;
        }
    }
}
