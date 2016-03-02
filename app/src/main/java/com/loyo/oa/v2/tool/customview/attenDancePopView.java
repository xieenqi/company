package com.loyo.oa.v2.tool.customview;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

import com.loyo.oa.v2.R;

/**
 * 加班提示框
 * Created by yyy on 16/2/19.
 */
public class AttenDancePopView extends Dialog{


    public AttenDancePopView(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.attendancepopview);

    }

    public void generalOutBtn(View.OnClickListener cusOnclick){
        findViewById(R.id.attendance_generalout).setOnClickListener(cusOnclick);
    }

    public void finishOutBtn(View.OnClickListener cusOnclick){
        findViewById(R.id.attendance_finishout).setOnClickListener(cusOnclick);
    }

    public void cancels(View.OnClickListener cusOnclick){
        findViewById(R.id.attendance_cancel).setOnClickListener(cusOnclick);
    }


}
