package com.liuzhuang.autofitgridlayout.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.liuzhuang.acgridlayout.AnimateCalendarGridLayout;
import com.liuzhuang.autofitgridlayout.R;

public class CalendarAdapter extends AnimateCalendarGridLayout.CalendarAdapter {

    private final static int SELECTED_ANIMATION_DURATION = 350;
    private int mUnselectedTextColor;

    public CalendarAdapter(final Context context) {
        super(context);

        mUnselectedTextColor = context.getResources().getColor(R.color.gray);
    }

    @Override
    public void onSelected(final View child) {
        final ViewHolder viewHolder = new ViewHolder(child);
        viewHolder.selected.setAlpha(0f);
        viewHolder.selected.setScaleX(0f);
        viewHolder.selected.setScaleY(0f);
        viewHolder.selected.setVisibility(View.VISIBLE);
        ViewCompat.animate(viewHolder.selected)
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(SELECTED_ANIMATION_DURATION)
                .withLayer()
                .start();
        viewHolder.day.setTextColor(Color.BLACK);
    }

    @Override
    public void onUnselected(final View child) {
        final ViewHolder viewHolder = new ViewHolder(child);
        viewHolder.selected.setAlpha(1f);
        viewHolder.selected.setScaleX(1f);
        viewHolder.selected.setScaleY(1f);
        viewHolder.selected.setVisibility(View.VISIBLE);
        ViewCompat.animate(viewHolder.selected)
                .alpha(0f)
                .scaleX(0f)
                .scaleY(0f)
                .setDuration(SELECTED_ANIMATION_DURATION)
                .withLayer()
                .start();
        viewHolder.day.setTextColor(mUnselectedTextColor);
    }

    @Override
    public void onNormal(final View child) {
        final ViewHolder viewHolder = new ViewHolder(child);
        viewHolder.selected.setAlpha(0f);
        viewHolder.selected.setScaleX(0f);
        viewHolder.selected.setScaleY(0f);
        viewHolder.selected.setVisibility(View.GONE);
        viewHolder.day.setTextColor(mUnselectedTextColor);
    }

    @Override
    public View initChild(final LayoutInflater layoutInflater, final int day) {
        final View convertView = layoutInflater.inflate(R.layout.item_calendar, null, false);
        final ViewHolder viewHolder = new ViewHolder(convertView);

        viewHolder.day.setText(String.valueOf(day));
        viewHolder.selected.setVisibility(View.GONE);

        return convertView;
    }

    public static class ViewHolder {
        public final TextView day;
        public final View selected;

        public ViewHolder(final View view) {
            this.day = (TextView) view.findViewById(R.id.txt_item_calendar_day);
            this.selected = view.findViewById(R.id.img_item_calendar_selected);
        }
    }
}