package com.loyo.oa.v2.activity.home.adapter;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.loyo.oa.v2.R;

import java.util.Random;

/**
 * 添加 过程统计 数据
 * Created by xeq on 16/6/16.
 */
public class ProcessDataAdapter extends LinearLayout {
    private Context mContext;
    View view;
    TextView tv_name, tv_number;
    ProgressBar pb_progress;
    private int[] colors = {R.drawable.shape_progressbar_mini20, R.drawable.shape_progressbar_mini21, R.drawable.shape_progressbar_mini22,
            R.drawable.shape_progressbar_mini23, R.drawable.shape_progressbar_mini24, R.drawable.shape_progressbar_mini25,
            R.drawable.shape_progressbar_mini26, R.drawable.shape_progressbar_mini27, R.drawable.shape_progressbar_mini28,
            R.drawable.shape_progressbar_mini29};

    public ProcessDataAdapter(Context context, String name, int value, int max, int index) {
        this(context, null, 0);
        bindView(name, value, max, index);
    }

    public ProcessDataAdapter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        setOrientation(VERTICAL);
        setPadding(0, 10, 0, 10);
        view = LayoutInflater.from(mContext).inflate(R.layout.item_process, null);
        tv_name = (TextView) view.findViewById(R.id.tv_name);
        pb_progress = (ProgressBar) view.findViewById(R.id.pb_progress);
        tv_number = (TextView) view.findViewById(R.id.tv_number);
    }

    private void bindView(String name, int value, int max, int index) {
        tv_name.setText(name);
        tv_number.setText(value + "次");
        pb_progress.setMax(max);
        pb_progress.setProgress(value);
        pb_progress.setProgressDrawable(getResources().getDrawable(colors[index]));
        this.addView(view);
    }

    private int getRandomNumber() {
        Random random = new Random();
        for (int i = 0; i < 9; i++) {
            System.out.println(Math.abs(random.nextInt()) % 9);
            return Math.abs(random.nextInt()) % 9;
        }
        return 0;
    }
}
