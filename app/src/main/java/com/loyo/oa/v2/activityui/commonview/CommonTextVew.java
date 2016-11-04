package com.loyo.oa.v2.activityui.commonview;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;

/**
 * Created by xeq on 16/11/3.
 */

public class CommonTextVew extends LinearLayout {
    public CommonTextVew(Context context, String text) {
        super(context);
        this.removeAllViews();
        TextView view = (TextView) LayoutInflater.from(context).inflate(R.layout.item_text, null);
        view.setText(text);
        this.addView(view);
        this.setGravity(Gravity.CENTER_VERTICAL);
    }

    public CommonTextVew(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CommonTextVew(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
