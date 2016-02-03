package com.liuzhuang.acgridlayout;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by GIGAMOLE on 26.11.2015.
 */
public class AnimateCalendarGridLayout extends ViewGroup {

    private static final int ANIMATION_DURATION = 350;
    private final int columnCount = 7;
    private int verticalSpace;
    private int horizontalSpace;
    private int childWidth;

    private CalendarAdapter mCalendarAdapter;

    private ArrayList<View> notGoneViewList;

    private ArrayList<Child> mChildren = new ArrayList<>();

    private OnChangeListener mOnChangeListener;
    private OnDateSelectedListener mOnDateSelectedListener;

    private int mOffset = -1;
    private int mLastOffset = -1;
    private int mLastDayCount;

    private Calendar mCalendar;

    private boolean mIsChildrenInflate;
    private boolean mIsChanging;

    public AnimateCalendarGridLayout(Context context) {
        super(context);
    }

    public AnimateCalendarGridLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AnimateCalendarGridLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void refreshNotGoneChildList() {
        if (notGoneViewList == null) {
            notGoneViewList = new ArrayList<View>();
        }
        notGoneViewList.clear();
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                notGoneViewList.add(child);
            }
        }
    }

    public OnDateSelectedListener getOnDateSelectedListener() {
        return mOnDateSelectedListener;
    }

    public void setOnDateSelectedListener(final OnDateSelectedListener onDateSelectedListener) {
        mOnDateSelectedListener = onDateSelectedListener;
    }

    public CalendarAdapter getCalendarAdapter() {
        return mCalendarAdapter;
    }

    public void setCalendarAdapter(final CalendarAdapter calendarAdapter, final Calendar calendar) {
        if (calendarAdapter == null)
            throw new NullPointerException("FilterAdapter must be provided");

        mCalendarAdapter = calendarAdapter;

        final LayoutInflater layoutInflater = LayoutInflater.from(calendarAdapter.mContext);

        final ArrayList<View> children = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            final View child = calendarAdapter.initChild(
                    layoutInflater,
                    i
            );
            child.setVisibility(GONE);
            child.setEnabled(false);

            children.add(child);
        }
        for (int i = 1; i < 32; i++) {
            final View child = calendarAdapter.initChild(
                    layoutInflater,
                    i
            );
            child.setTag(i);
            child.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(final View v) {
                    setSelection((Integer) v.getTag());
                }
            });
            children.add(child);
        }

        mIsChildrenInflate = false;
        mChildren.clear();

        removeAllViews();

        for (View child : children)
            addView(child);

        final int originalDay = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        final int mDayOfWeekStart = calendar.get(Calendar.DAY_OF_WEEK);
        final int mWeekStart = calendar.getFirstDayOfWeek();
        calendar.set(Calendar.DAY_OF_MONTH, originalDay);

        final int mNumDays = 7;
        final int offset = (mDayOfWeekStart < mWeekStart ? (mDayOfWeekStart + mNumDays) : mDayOfWeekStart) - mWeekStart;
        mLastOffset = mOffset;
        mOffset = offset;

        for (int i = 0; i < 7; i++) {
            if (i < offset)
                getChildAt(i).setVisibility(INVISIBLE);
        }
        mLastDayCount = calendar.getActualMaximum(Calendar.DAY_OF_MONTH) + 7;
        for (int i = calendar.getActualMaximum(Calendar.DAY_OF_MONTH) + 7; i < getChildCount(); i++)
            getChildAt(i).setVisibility(INVISIBLE);

        mCalendar = calendar;
        setSelection(calendar.get(Calendar.DAY_OF_MONTH));
    }

    public void setSelection(int day) {
        final CalendarAdapter calendarAdapter = getCalendarAdapter();
        final int originalDay = day;
        day += 6;
        final boolean isSame = day == calendarAdapter.mSelection;
        if (!isSame) {
            calendarAdapter.mLastSelection = calendarAdapter.mSelection;
            calendarAdapter.mSelection = day;

            if (mOnDateSelectedListener != null) {
                mCalendar.set(Calendar.DAY_OF_MONTH, originalDay);
                mOnDateSelectedListener.onDateSelect(mCalendar);
            }

            for (int i = 7; i < getChildCount(); i++) {
                final View child = getChildAt(i);
                if (i == calendarAdapter.mSelection)
                    calendarAdapter.onSelected(child);
                else if (i == calendarAdapter.mLastSelection)
                    calendarAdapter.onUnselected(child);
                else
                    calendarAdapter.onNormal(child);
            }
        }
    }

    public OnChangeListener getOnChangeListener() {
        return mOnChangeListener;
    }

    public void setOnChangeListener(final OnChangeListener onChangeListener) {
        mOnChangeListener = onChangeListener;
    }

    public int getVerticalSpace() {
        return verticalSpace;
    }

    public void setVerticalSpace(int verticalSpace) {
        this.verticalSpace = verticalSpace;
        requestLayout();
    }

    public int getHorizontalSpace() {
        return horizontalSpace;
    }

    public void setHorizontalSpace(int horizontalSpace) {
        this.horizontalSpace = horizontalSpace;
        requestLayout();
    }

    public boolean isChanging() {
        return mIsChanging;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        refreshNotGoneChildList();
        if (childWidth <= 0) {
            final int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
            childWidth = (int) (
                    (parentWidth - (columnCount - 1) * horizontalSpace * 1.0f) / columnCount + 0.5f);
        }
        int childCount = notGoneViewList.size();
        int line = childCount % columnCount == 0 ? childCount / columnCount
                : childCount / columnCount + 1;
        int totalHeight = 0;
        int childIndex = 0;
        for (int i = 0; i < line; i++) {
            int inlineHeight = 0;
            for (int j = 0; j < columnCount; j++) {
                childIndex = i * columnCount + j;
                if (childIndex < childCount) {
                    View child = notGoneViewList.get(childIndex);
                    int childWidthWithPadding = childWidth;
                    if (j == 0) {
                        // measureChild会在size的基础上减掉paddingLeft和paddingRight，对于每一行第一个元素加上paddingRight抵消
                        childWidthWithPadding += getPaddingRight();
                    } else if (j == columnCount - 1) {
                        // measureChild会在size的基础上减掉paddingLeft和paddingRight，对于每一行最后一个元素加上paddingLeft抵消
                        childWidthWithPadding += getPaddingLeft();
                    }
                    measureChild(child,
                            MeasureSpec.makeMeasureSpec(childWidthWithPadding, MeasureSpec.EXACTLY),
                            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                    int totalInlineChildHeight = child.getMeasuredHeight();
                    if (totalInlineChildHeight > inlineHeight) {
                        inlineHeight = totalInlineChildHeight;
                    }
                }
            }
            totalHeight += inlineHeight;
            totalHeight += verticalSpace;
        }
        totalHeight -= verticalSpace;
        totalHeight += getPaddingTop() + getPaddingBottom();
        setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec),
                totalHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = notGoneViewList.size();
        int line = childCount % columnCount == 0 ? childCount / columnCount
                : childCount / columnCount + 1;
        int childIndex = 0;
        int lastLeft = getPaddingLeft();
        int lastTop = getPaddingTop();
        for (int i = 0; i < line; i++) {
            int inlineHeight = 0;
            for (int j = 0; j < columnCount; j++) {
                childIndex = i * columnCount + j;
                if (childIndex < childCount) {
                    View child = notGoneViewList.get(childIndex);
                    int childWidth = child.getMeasuredWidth();
                    int childHeight = child.getMeasuredHeight();

                    child.layout(lastLeft, lastTop, lastLeft + childWidth, lastTop + childHeight);
                    lastLeft += (childWidth + horizontalSpace);
                    int totalInlineChildHeight = child.getMeasuredHeight();
                    if (totalInlineChildHeight > inlineHeight) {
                        inlineHeight = totalInlineChildHeight;
                    }

                    if (!mIsChildrenInflate)
                        mChildren.add(new Child(child));
                }
            }
            lastLeft = getPaddingLeft();
            lastTop += (inlineHeight + verticalSpace);
        }

        mIsChildrenInflate = true;
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    public void setDate(final Calendar calendar) {
        if (getCalendarAdapter() == null)
            throw new NullPointerException("CalendarAdapter not provided");

        if (mIsChanging)
            return;

        mCalendar = calendar;

        final int originalDay = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        final int mDayOfWeekStart = calendar.get(Calendar.DAY_OF_WEEK);
        final int mWeekStart = calendar.getFirstDayOfWeek();
        calendar.set(Calendar.DAY_OF_MONTH, originalDay);

        final int mNumDays = 7;
        final int offset = (mDayOfWeekStart < mWeekStart ? (mDayOfWeekStart + mNumDays) : mDayOfWeekStart) - mWeekStart;
        mLastOffset = mOffset;
        mOffset = offset;

        final OnChangeListener onChangeListener = getOnChangeListener();
        if (onChangeListener != null) {
            mIsChanging = true;
            onChangeListener.onChangeStart();
        }

        animate(calendar.getActualMaximum(Calendar.DAY_OF_MONTH) + 7);
        if (getCalendarAdapter().mSelection > mLastDayCount - 1)
            setSelection(1);

        postDelayed(new Runnable() {
            @Override
            public void run() {
                if (onChangeListener != null) {
                    mIsChanging = false;
                    onChangeListener.onChangeFinish();
                }
            }
        }, ANIMATION_DURATION);
    }

    private void animate(final int dayCount) {
        for (int i = 7, j = 0; i < mLastDayCount; i++, j++) {
            final int fromIndex = j + mLastOffset;
            final int toIndex = j + mOffset;
            if (fromIndex >= mChildren.size() || toIndex >= mChildren.size())
                break;

            final View child = getChildAt(i);
            final Child from = mChildren.get(fromIndex);
            final Child to = mChildren.get(toIndex);

            final Animation animation = new TranslateAnimation(
                    from.getLeft() - to.getLeft(),
                    0.0f,
                    from.getTop() - to.getTop(),
                    0.0f
            );
            animation.setFillEnabled(true);
            animation.setFillBefore(true);
            animation.setFillAfter(true);
            animation.setDuration(ANIMATION_DURATION);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(final Animation animation) {
                }

                @Override
                public void onAnimationEnd(final Animation animation) {
                    child.clearAnimation();
                }

                @Override
                public void onAnimationRepeat(final Animation animation) {

                }
            });
            child.startAnimation(animation);
        }
        for (int i = 7; i < dayCount; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                final Animation animation = new AlphaAnimation(0.0f, 1.0f);
                animation.setDuration(ANIMATION_DURATION);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(final Animation animation) {
                        child.setVisibility(VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(final Animation animation) {
                        child.clearAnimation();
                    }

                    @Override
                    public void onAnimationRepeat(final Animation animation) {

                    }
                });
                child.startAnimation(animation);
            }
        }

        for (int i = dayCount; i < getChildCount(); i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() == VISIBLE) {
                final Animation animation = new AlphaAnimation(1.0f, 0.0f);
                animation.setDuration(ANIMATION_DURATION);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(final Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(final Animation animation) {
                        child.setVisibility(GONE);
                        child.clearAnimation();
                    }

                    @Override
                    public void onAnimationRepeat(final Animation animation) {

                    }
                });
                child.startAnimation(animation);
            }
        }

        for (int i = 0; i < 7; i++)
            if (i < mOffset)
                getChildAt(i).setVisibility(INVISIBLE);
            else
                getChildAt(i).setVisibility(GONE);

        mLastDayCount = dayCount;
    }

    private void getLayout(View view, Rect rect) {
        rect.set(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
    }

    public interface OnChangeListener {
        void onChangeStart();

        void onChangeFinish();
    }

    public interface OnDateSelectedListener {
        void onDateSelect(final Calendar calendar);
    }

    public static class LayoutParams extends MarginLayoutParams {

        /**
         * {@inheritDoc}
         */
        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        /**
         * {@inheritDoc}
         */
        public LayoutParams(int width, int height) {
            super(width, height);
        }


        /**
         * {@inheritDoc}
         */
        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        /**
         * {@inheritDoc}
         */
        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        /**
         * Copy constructor. Clones the width, height, margin values, and
         * gravity of the source.
         *
         * @param source The layout params to copy from.
         */
        public LayoutParams(LayoutParams source) {
            super(source);
        }

    }

    public static abstract class CalendarAdapter {

        private int mSelection;
        private int mLastSelection;

        private Context mContext;

        public CalendarAdapter(final Context context) {
            initContext(context);
        }

        private void initContext(final Context context) {
            mContext = context;
        }

        public abstract void onSelected(final View child);

        public abstract void onUnselected(final View child);

        public abstract void onNormal(final View child);

        public abstract View initChild(final LayoutInflater layoutInflater, final int position);
    }

    private class Child {
        private float mLeft;
        private float mTop;
        private float mRight;
        private float mBottom;

        public Child(final View child) {
            final Rect childRect = new Rect();
            childRect.set(child.getLeft(), child.getTop(), child.getRight(), child.getBottom());

            mLeft = childRect.left;
            mTop = childRect.top;
            mRight = childRect.right;
            mBottom = childRect.bottom;
        }

        public float getLeft() {
            return mLeft;
        }

        public float getTop() {
            return mTop;
        }

        public float getRight() {
            return mRight;
        }

        public float getBottom() {
            return mBottom;
        }
    }
}