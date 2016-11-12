package com.loyo.oa.v2.activityui.commonview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;

/**
 * Created by xeq on 16/11/12.
 */

public class CommonRecordItem extends LinearLayout implements View.OnClickListener {

    private Context context;
    private String path, time;
    private TextView tv_record_length, tv_record_number;

    public CommonRecordItem(Context context, String path, String time) {
        super(context);
        this.context = context;
        this.path = path;
        this.time = time;
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
        tv_record_length = (TextView) view.findViewById(R.id.tv_record_length);
        tv_record_number = (TextView) view.findViewById(R.id.tv_record_number);

        tv_record_length.setOnClickListener(this);
        this.removeAllViews();
        this.addView(view);
        setRecordLength();
    }

    private void setRecordLength() {
        tv_record_number.setText(time + "'");
        int timeNumber = Integer.parseInt(time);
        if (timeNumber > 0 && timeNumber <= 15) {
            tv_record_length.setText("000");
        } else if (timeNumber > 15 && timeNumber <= 30) {
            tv_record_length.setText("00000");
        } else if (timeNumber > 30 && timeNumber <= 45) {
            tv_record_length.setText("0000000");
        } else if (timeNumber > 45 && timeNumber <= 60) {
            tv_record_length.setText("000000000");
        } else if (timeNumber > 60 && timeNumber <= 75) {
            tv_record_length.setText("00000000000");
        } else {
            tv_record_length.setText("");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_record_length:
                RecordUtils.getInstance(context).voicePlay(path);
                break;
        }
    }
}
