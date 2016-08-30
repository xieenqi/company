package com.loyo.oa.v2.activityui.worksheet;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.tool.BaseActivity;

/**
 * 【提交完成】工单
 * Created by yyy on 16/8/30.
 */
public class WorksheetSubmitActivity extends BaseActivity implements View.OnClickListener{

    private TextView tv_title_1;
    private RelativeLayout img_title_right;
    private LinearLayout   img_title_left;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worksheet_submit);
        initUI();
    }

    private void initUI(){
        tv_title_1 = (TextView) findViewById(R.id.tv_title_1);
        img_title_right = (RelativeLayout) findViewById(R.id.img_title_right);
        img_title_left  = (LinearLayout) findViewById(R.id.img_title_left);
        super.setTitle("提交完成");

        img_title_right.setOnClickListener(this);
        img_title_left.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.img_title_right:
                Toast("提交");
                break;

            case R.id.img_title_left:
                onBackPressed();
                break;

            default:
                break;
        }

    }
}
