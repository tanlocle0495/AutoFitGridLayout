package com.liuzhuang.autofitgridlayout.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ScrollView;

import com.liuzhuang.afgridlayout.AnimateFilterGridLayout;
import com.liuzhuang.autofitgridlayout.R;
import com.liuzhuang.autofitgridlayout.adapters.ColorsAdapter;
import com.liuzhuang.autofitgridlayout.data.ColorModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class FilterActivity extends AppCompatActivity implements View.OnClickListener, AnimateFilterGridLayout.OnFilterListener {

    private View mBtnRandom;

    private CheckBox mCbLeft;
    private CheckBox mCbCenter;
    private CheckBox mCbRight;

    private ScrollView mScrollView;
    private AnimateFilterGridLayout mAnimateFilterGridLayout;
    private ColorsAdapter mColorsAdapter;

    private int mLeftColor;
    private int mCenterColor;
    private int mRightColor;
    private Set<Integer> mColors = new HashSet<>();

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        initUI();
        setUI();
    }


    private void initUI() {
        mBtnRandom = findViewById(R.id.btn_random);

        mCbLeft = (CheckBox) findViewById(R.id.cb_filter_left);
        mCbCenter = (CheckBox) findViewById(R.id.cb_filter_center);
        mCbRight = (CheckBox) findViewById(R.id.cb_filter_right);

        mAnimateFilterGridLayout = (AnimateFilterGridLayout) findViewById(R.id.afgl_colors);
        mScrollView = (ScrollView) findViewById(R.id.sv);
    }

    private void setUI() {
        mLeftColor = getResources().getColor(R.color.primary);
        mCenterColor = getResources().getColor(R.color.black);
        mRightColor = getResources().getColor(R.color.gray);

        mBtnRandom.setOnClickListener(this);
        mAnimateFilterGridLayout.setOnFilterListener(this);

        final View.OnClickListener cbOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final CheckBox checkBox = (CheckBox) v;
                final Integer color = (Integer) v.getTag();
                if (checkBox.isChecked())
                    mColors.add(color);
                else
                    mColors.remove(color);

                mAnimateFilterGridLayout.filter();
            }
        };
        mCbLeft.setOnClickListener(cbOnClickListener);
        mCbLeft.setTag(mLeftColor);
        mCbCenter.setOnClickListener(cbOnClickListener);
        mCbCenter.setTag(mCenterColor);
        mCbRight.setOnClickListener(cbOnClickListener);
        mCbRight.setTag(mRightColor);

        toggleCb(false);
    }

    private void toggleCb(final boolean isEnabled) {
        mCbLeft.setEnabled(isEnabled);
        mCbCenter.setEnabled(isEnabled);
        mCbRight.setEnabled(isEnabled);
    }

    private void resetFilter() {
        mColors.clear();
        mCbLeft.setChecked(false);
        mCbCenter.setChecked(false);
        mCbRight.setChecked(false);
        toggleCb(true);
    }

    private void setNewSchedulePlaces() {
        final ArrayList<ColorModel> colorModels = new ArrayList<>();
        final int size = new Random().nextInt(40);

        final Random randomTechnology = new Random();
        for (int i = 0; i < size; i++) {
            final int color;
            switch (randomTechnology.nextInt(3)) {
                default:
                case 0:
                    color = mLeftColor;
                    break;
                case 1:
                    color = mCenterColor;
                    break;
                case 2:
                    color = mRightColor;
                    break;
            }

            colorModels.add(new ColorModel(i, color));
        }

        mColorsAdapter = new ColorsAdapter(getBaseContext(), colorModels);
        mAnimateFilterGridLayout.setFilterAdapter(mColorsAdapter);
        mAnimateFilterGridLayout.filter();
    }

    @Override
    public void onFilterStart() {
        mScrollView.smoothScrollTo(0, 0);
        toggleCb(false);
    }

    @Override
    public void onFilterFinish() {
        toggleCb(true);
    }

    @Override
    public ArrayList<?> onFilterSet(final ArrayList<?> originalItems, final ArrayList<?> lastItems, final ArrayList<Integer> positionsToAdd, final ArrayList<Integer> positionsToRemove) {
        final ArrayList<ColorModel> colorModels = new ArrayList<>();
        if (mColors.size() != 0)
            for (int i = 0; i < originalItems.size(); i++) {
                final ColorModel tempItem = (ColorModel) originalItems.get(i);
                if (mColors.contains(tempItem.color))
                    colorModels.add(tempItem);
            }
        else
            colorModels.addAll((Collection<? extends ColorModel>) originalItems);

        for (int i = 0; i < originalItems.size(); i++) {
            final ColorModel tempItem = (ColorModel) originalItems.get(i);
            final boolean isContainsTechnology = mColors.size() == 0 || mColors.contains(tempItem.color);
            final boolean isContainsItem = lastItems.contains(tempItem);
            if (isContainsTechnology && !isContainsItem)
                positionsToAdd.add(i);
            else if (!isContainsTechnology && isContainsItem)
                positionsToRemove.add(i);
        }

        return colorModels;
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.btn_random:
                resetFilter();
                setNewSchedulePlaces();
                break;
        }
    }
}
