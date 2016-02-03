package com.liuzhuang.afgridlayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;

import java.util.ArrayList;

/**
 * Created by GIGAMOLE on 26.11.2015.
 */
public class AnimateFilterGridLayout extends ViewGroup {

    private static final int ANIMATION_DURATION = 350;

    private int verticalSpace;

    private int horizontalSpace;

    private int columnCount;

    private int childWidth;

    private FilterAdapter mFilterAdapter;

    private ArrayList<View> notGoneViewList;

    private ArrayList<Child> mChildren = new ArrayList<>();

    private OnFilterListener mOnFilterListener;

    private boolean mIsChildrenInflate;

    private boolean mIsFiltering;
    private AdapterView.OnItemClickListener mOnItemClickListener;

    public AnimateFilterGridLayout(Context context) {
        super(context);
    }

    public AnimateFilterGridLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public AnimateFilterGridLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AnimateFilterGridLayout);
        columnCount = a.getInt(R.styleable.AnimateFilterGridLayout_afgl_columnCount, 1);
        horizontalSpace = a.getDimensionPixelSize(R.styleable.AnimateFilterGridLayout_afgl_horizontalSpace, 0);
        verticalSpace = a.getDimensionPixelSize(R.styleable.AnimateFilterGridLayout_afgl_verticalSpace, 0);
        a.recycle();
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

    public FilterAdapter getFilterAdapter() {
        return mFilterAdapter;
    }

    public void setFilterAdapter(final FilterAdapter filterAdapter) {
        if (filterAdapter == null)
            throw new NullPointerException("FilterAdapter must be provided");

        mFilterAdapter = filterAdapter;
        final LayoutInflater layoutInflater = LayoutInflater.from(filterAdapter.mContext);

        final ArrayList<View> children = new ArrayList<>();
        for (int i = 0; i < filterAdapter.getCount(); i++) {
            final View child = filterAdapter.initChild(layoutInflater, i);
            final int finalIndex = i;

            child.setOnClickListener(
                    new OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            if (mOnItemClickListener != null)
                                mOnItemClickListener.onItemClick(null, child, finalIndex, finalIndex);
                        }
                    }
            );
            children.add(child);
        }

        mIsChildrenInflate = false;
        mChildren.clear();

        removeAllViews();

        for (View child : children)
            addView(child);
    }

    public AdapterView.OnItemClickListener getOnItemClickListener() {
        return mOnItemClickListener;
    }

    public void setOnItemClickListener(final AdapterView.OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public OnFilterListener getOnFilterListener() {
        return mOnFilterListener;
    }

    public void setOnFilterListener(final OnFilterListener onFilterListener) {
        mOnFilterListener = onFilterListener;
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

    public int getColumnCount() {
        return columnCount;
    }

    public void setColumnCount(int columnCount) {
        this.columnCount = columnCount;
        requestLayout();
    }

    public boolean isFiltering() {
        return mIsFiltering;
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

    public void filter() {
        if (getFilterAdapter() == null)
            throw new NullPointerException("FilterAdapter not provided");

        if (mIsFiltering)
            return;

        final ArrayList<Integer> positionsToAdd = new ArrayList<>();
        final ArrayList<Integer> positionsToRemove = new ArrayList<>();

        final OnFilterListener onFilterListener = getOnFilterListener();
        if (onFilterListener != null) {
            mFilterAdapter.mLastItems = onFilterListener.onFilterSet(
                    mFilterAdapter.getItems(),
                    mFilterAdapter.getLastItems(),
                    positionsToAdd,
                    positionsToRemove
            );
        }

        if (onFilterListener != null) {
            mIsFiltering = true;
            onFilterListener.onFilterStart();
        }

        final boolean isForRemove = positionsToRemove.size() > positionsToAdd.size();
        if (isForRemove)
            animatedRemove(positionsToRemove);
        else
            animateAdd(positionsToAdd);

        postDelayed(new Runnable() {
            @Override
            public void run() {
                if (onFilterListener != null) {
                    mIsFiltering = false;
                    onFilterListener.onFilterFinish();
                }
            }
        }, ANIMATION_DURATION);
    }

    private void animatedRemove(final ArrayList<Integer> positionsToRemove) {
        int positionToMoveCounter = 0;
        final ArrayList<Integer> visiblePositions = new ArrayList<>();
        for (int i = 0; i < getChildCount(); i++) {
            final View currentChild = getChildAt(i);
            if (currentChild.getVisibility() != GONE)
                visiblePositions.add(i);
            if (positionsToRemove.contains(i)) {
                final int removeChildPosition = positionsToRemove.get(positionsToRemove.indexOf(i));
                final View removeChild = getChildAt(removeChildPosition);
                final Animation animation = new AlphaAnimation(1.0f, 0.0f);
                animation.setDuration(ANIMATION_DURATION);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(final Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(final Animation animation) {
                        removeChild.setVisibility(GONE);
                        removeChild.clearAnimation();
                    }

                    @Override
                    public void onAnimationRepeat(final Animation animation) {

                    }
                });
                removeChild.startAnimation(animation);
            }
        }

        for (int i = 0; i < getChildCount(); i++) {
            final View currentChild = getChildAt(i);
            if (currentChild.getAnimation() != null)
                continue;
            if (currentChild.getVisibility() == GONE)
                continue;
            if (positionToMoveCounter == visiblePositions.size())
                break;
            final View from = getChildAt(i);
            final View to = getChildAt(visiblePositions.get(positionToMoveCounter++));

            final Rect fromRect = new Rect();
            getLayout(from, fromRect);
            final Rect toRect = new Rect();
            getLayout(to, toRect);

            final Animation animation = new TranslateAnimation(
                    0.0f,
                    toRect.left - fromRect.left,
                    0.0f,
                    toRect.top - fromRect.top
            );
            animation.setFillEnabled(true);
            animation.setFillBefore(true);
            animation.setFillAfter(true);
            animation.setDuration(ANIMATION_DURATION);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(final Animation animation) {
                    from.setVisibility(VISIBLE);
                }

                @Override
                public void onAnimationEnd(final Animation animation) {
                    from.clearAnimation();
                }

                @Override
                public void onAnimationRepeat(final Animation animation) {

                }
            });

            from.startAnimation(animation);
        }
    }

    private void animateAdd(final ArrayList<Integer> positionsToAdd) {
        final ArrayList<Integer> visiblePositions = new ArrayList<>();
        for (int i = 0; i < getChildCount(); i++) {
            if (positionsToAdd.contains(i)) {
                final int addChildPosition = positionsToAdd.get(positionsToAdd.indexOf(i));
                final View addChild = getChildAt(addChildPosition);
                addChild.setVisibility(INVISIBLE);

                final Animation animation = new AlphaAnimation(0.0f, 1.0f);
                animation.setDuration(ANIMATION_DURATION);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(final Animation animation) {
                        addChild.setVisibility(VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(final Animation animation) {
                        addChild.clearAnimation();
                    }

                    @Override
                    public void onAnimationRepeat(final Animation animation) {

                    }
                });
                addChild.startAnimation(animation);
            }

            final View child = getChildAt(i);
            if (child.getVisibility() != GONE)
                visiblePositions.add(i);
        }

        final ArrayList<Integer> toPositions = new ArrayList<>();
        for (int i = 0; i < visiblePositions.size(); i++) {
            final int visiblePosition = visiblePositions.get(i);
            final View child = getChildAt(visiblePosition);
            if (child.getVisibility() != INVISIBLE)
                toPositions.add(i);
        }

        int positionFromMoveCounter = 0;
        int positionToMoveCounter = 0;
        for (int i = 0; i < getChildCount(); i++) {
            final View currentChild = getChildAt(i);
            if (currentChild.getAnimation() != null)
                continue;
            if (currentChild.getVisibility() == GONE)
                continue;
            if (positionToMoveCounter == toPositions.size())
                return;

            final Child from = mChildren.get(positionFromMoveCounter++);
            final Child to = mChildren.get(toPositions.get(positionToMoveCounter++));

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
                    currentChild.clearAnimation();
                }

                @Override
                public void onAnimationRepeat(final Animation animation) {

                }
            });

            currentChild.startAnimation(animation);
        }
    }

    private void getLayout(View view, Rect rect) {
        rect.set(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
    }

    public interface OnFilterListener {
        void onFilterStart();

        void onFilterFinish();

        ArrayList<?> onFilterSet(
                final ArrayList<?> originalItems,
                final ArrayList<?> lastItems,
                final ArrayList<Integer> positionsToAdd,
                final ArrayList<Integer> positionsToRemove
        );
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

    public static abstract class FilterAdapter {

        private Context mContext;

        private ArrayList<?> mItems = new ArrayList<>();

        private ArrayList<?> mLastItems = new ArrayList<>();

        private int mCount;

        public FilterAdapter(final Context context, final ArrayList<?> items) {
            initContext(context);
            initItems(items);
        }

        private void initContext(final Context context) {
            mContext = context;
        }

        private void initItems(final ArrayList<?> items) {
            mItems = items;
            mCount = items.size();
        }

        public final int getCount() {
            return mCount;
        }

        public final ArrayList<?> getItems() {
            return mItems;
        }

        private ArrayList<?> getLastItems() {
            return mLastItems;
        }

        private void setLastItems(final ArrayList<?> lastItems) {
            mLastItems = lastItems;
        }

        public final Object getItem(final int position) {
            return mItems.get(position);
        }

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