package com.loyo.oa.dropdownmenu.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loyo.oa.dropdownmenu.utils.UIUtil;
import com.loyo.oa.v2.R;

import java.util.Arrays;
import java.util.List;

/**
 * TODO: document your custom view class.
 */
public class MenuTabBar extends LinearLayout {
    private Context context;

    /* 分割线 */
    private Paint mDividerPaint;
    private int mDividerColor = 0xFFd2d2d2; // 分割线颜色
    private int mDividerPadding = 13;       // 分割线距离上下padding

    /* 上下两条线 */
    private Paint mLinePaint;
    private float mLineHeight = 1;
    private int mLineColor = 0xFFd2d2d2;


    private int mTabTextSize = 15; // 指针文字的大小,sp
    private int mTabDefaultColor = 0xFF333333; // 未选中默认颜色
    private int mTabSelectedColor = 0xFF008DF2; // 指针选中颜色
    private int drawableRight = 10;

    private int measureHeight;
    private int measuredWidth;

    private int mTabCount; // 设置的条目数量
    private int mCurrentIndicatorPosition; // 当前指针选中条目
    private int mLastIndicatorPosition; // 上一个指针选中条目
    private OnTabClickListener mOnTabClickListener;


    public MenuTabBar(Context context) {
        this(context, null);
    }

    public MenuTabBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MenuTabBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }


    /** Tab点击事件 */
    public interface OnTabClickListener {
        void onTabClick(View v, int position, boolean isOpen);
    }

    public void setOnItemClickListener(OnTabClickListener listener) {
        this.mOnTabClickListener = listener;
    }

    private void init(Context context) {
        this.context = context;
        setOrientation(LinearLayout.HORIZONTAL);
        setBackgroundColor(Color.WHITE);
        setWillNotDraw(false);

        mDividerPaint = new Paint();
        mDividerPaint.setAntiAlias(true);
        mDividerPaint.setColor(mDividerColor);

        mLinePaint = new Paint();
        mLinePaint.setColor(mLineColor);

        mDividerPadding = UIUtil.dp(context, mDividerPadding);
        drawableRight = UIUtil.dp(context, drawableRight);

        mLastIndicatorPosition = -1;
        mCurrentIndicatorPosition = -1;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureHeight = getMeasuredHeight();
        measuredWidth = getMeasuredWidth();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        /* 画分割线 */
        for (int i = 0; i < mTabCount - 1; ++i) {
            final View child = getChildAt(i);
            if (child == null || child.getVisibility() == View.GONE) {
                continue;
            }
            canvas.drawLine(child.getRight(), mDividerPadding, child.getRight(), measureHeight - mDividerPadding, mDividerPaint);
        }

        /* 上边线 */
        canvas.drawRect(0, 0, measuredWidth, mLineHeight, mLinePaint);

        /* 下边线 */
        canvas.drawRect(0, measureHeight - mLineHeight, measuredWidth, measureHeight, mLinePaint);
    }

    public void setTitles(List<String> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        this.removeAllViews();

        mTabCount = list.size();
        for (int pos = 0; pos < mTabCount; ++pos) {
            addView(generateTextView(list.get(pos), pos));
        }

        postInvalidate();
    }

    public void setTitles(String[] list) {
        setTitles(Arrays.asList(list));
    }

    private void switchTab(int pos) {
        TextView tv = getTabTitleViewAtPosition(pos);
        if (tv == null) {
            return;
        }

        Drawable drawable = tv.getCompoundDrawables()[2];
        int level = drawable.getLevel();

        if (mOnTabClickListener != null) {
            mOnTabClickListener.onTabClick(tv, pos, level == 1);
        }

        if (mLastIndicatorPosition == pos) {
            // 点击同一个条目时
            tv.setTextColor(level == 0 ? mTabSelectedColor : mTabDefaultColor);
            drawable.setLevel(1 - level);
        }
        else {
            mCurrentIndicatorPosition = pos;
            resetTabAtPosition(mLastIndicatorPosition);
            highlightTabAtPosition(mCurrentIndicatorPosition);
            mLastIndicatorPosition = pos;
        }
    }

    public void resetTabAtPosition(int pos) {
        TextView tv = getTabTitleViewAtPosition(pos);
        if (tv != null){
            tv.setTextColor(mTabDefaultColor);
            tv.getCompoundDrawables()[2].setLevel(0);
        }
    }

    public TextView getTabTitleViewAtPosition(int pos) {
        if (pos < 0 || pos >= mTabCount) {
            return null;
        }
        return (TextView) ((ViewGroup) getChildAt(pos)).getChildAt(0);
    }

    private View generateTextView(String title, int pos) {
        TextView tv = new TextView(context);
        tv.setGravity(Gravity.CENTER);
        tv.setText(title);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTabTextSize);
        tv.setTextColor(mTabDefaultColor);
        tv.setSingleLine();
        tv.setEllipsize(TextUtils.TruncateAt.END);
        tv.setMaxEms(8);
        Drawable drawable = getResources().getDrawable(R.drawable.level_drop_tab_arrow);
        tv.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
        tv.setCompoundDrawablePadding(drawableRight);

        // 将TextView添加到父控件RelativeLayout
        RelativeLayout rlContainer = new RelativeLayout(context);
        RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams(-2, -2);
        rlParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        rlContainer.addView(tv, rlParams);
        rlContainer.setId(pos);

        // 再将RelativeLayout添加到MenuTabBar中
        LayoutParams params = new LayoutParams(-1, -1);
        params.weight = 1;
        params.gravity = Gravity.CENTER;
        rlContainer.setLayoutParams(params);

        rlContainer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                switchTab(v.getId());
            }
        });

        return rlContainer;
    }

    public void highlightTabAtPosition(int pos) {
        TextView tv = getTabTitleViewAtPosition(pos);
        if (tv != null) {
            tv.setTextColor(mTabSelectedColor);
            tv.getCompoundDrawables()[2].setLevel(1);
        }
    }

    public int getCurrentIndicatorPosition() {
        return mCurrentIndicatorPosition;
    }

    public void setCurrentText(String text) {
        setTitleAtPosition(text, mCurrentIndicatorPosition);
    }

    public void setTitleAtPosition(String text, int position) {
        TextView tv = getTabTitleViewAtPosition(position);
        if (tv != null) {
            tv.setTextColor(mTabDefaultColor);
            tv.setText(text);
            tv.getCompoundDrawables()[2].setLevel(0);
        }
    }

    public int getLastIndicatorPosition() {
        return mLastIndicatorPosition;
    }
}