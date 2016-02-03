package com.liuzhuang.autofitgridlayout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.liuzhuang.afgridlayout.AnimateFilterGridLayout;

import java.util.ArrayList;

public class ColorsAdapter extends AnimateFilterGridLayout.FilterAdapter {

    private Context mContext;

    public ColorsAdapter(final Context context, final ArrayList<?> items) {
        super(context, items);
        mContext = context;
    }

    @Override
    public View initChild(final LayoutInflater layoutInflater, final int position) {
        final ColorModel colorModel = (ColorModel) getItem(position);

        final TextView colorView = (TextView) layoutInflater.inflate(R.layout.item_color, null, false);
        colorView.setTag(position);
        colorView.setText(String.valueOf(position));
        colorView.setBackgroundColor(colorModel.color);

        return colorView;
    }
}