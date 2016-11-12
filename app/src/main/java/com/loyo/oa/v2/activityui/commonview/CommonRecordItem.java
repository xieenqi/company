package com.loyo.oa.v2.activityui.commonview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.loyo.oa.v2.R;

/**
 * Created by xeq on 16/11/12.
 */

public class CommonRecordItem extends LinearLayout {

    private Context context;

    public CommonRecordItem(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    public CommonRecordItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CommonRecordItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initView() {
        View view = LayoutInflater.from(context).inflate(R.layout.view_common_record_item, null);
        LinearLayout.LayoutParams pl = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(pl);
        this.removeAllViews();
        this.addView(view);
    }
}
