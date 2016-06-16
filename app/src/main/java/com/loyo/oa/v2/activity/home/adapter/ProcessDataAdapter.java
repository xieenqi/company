package com.loyo.oa.v2.activity.home.adapter;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.loyo.oa.v2.R;

/**
 * 添加 过程统计 数据
 * Created by xeq on 16/6/16.
 */
public class ProcessDataAdapter extends LinearLayout {
    private Context mContext;


    public ProcessDataAdapter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        setLayoutParams(new ViewGroup.LayoutParams(-1, -2));
        setOrientation(HORIZONTAL);
        bindView();
    }

    public ProcessDataAdapter(Context context) {
        this(context, null, 0);

    }

    private void bindView() {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.item_process, null, false);
        ProgressBar pb_progress = (ProgressBar) view.findViewById(R.id.pb_progress);
        pb_progress.setProgress(86);
        pb_progress.setProgressDrawable(getResources().getDrawable(R.drawable.shape_progressbar_mini20));
    }
}
