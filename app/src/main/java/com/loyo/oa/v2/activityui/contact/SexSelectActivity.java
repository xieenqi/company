package com.loyo.oa.v2.activityui.contact;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.tool.BaseActivity;

/**
 * 性别选择页面
 * Created by xeq on 16/11/2.
 */

public class SexSelectActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout ll_man, ll_woman, img_title_left;
    private ImageView iv_man, iv_woman;
    private TextView tv_title_1;
    private static final int SEX_MAN = 1;
    private static final int SEX_WOMAN = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sex_select);
        initView();
    }

    private void initView() {
        String sex = getIntent().getStringExtra("sex");
        ll_man = (LinearLayout) findViewById(R.id.ll_man);
        ll_woman = (LinearLayout) findViewById(R.id.ll_woman);
        img_title_left = (LinearLayout) findViewById(R.id.img_title_left);
        iv_man = (ImageView) findViewById(R.id.iv_man);
        iv_woman = (ImageView) findViewById(R.id.iv_woman);
        tv_title_1 = (TextView) findViewById(R.id.tv_title_1);
        ll_man.setOnTouchListener(Global.GetTouch());
        ll_man.setOnClickListener(this);
        ll_woman.setOnTouchListener(Global.GetTouch());
        ll_woman.setOnClickListener(this);
        img_title_left.setOnTouchListener(Global.GetTouch());
        img_title_left.setOnClickListener(this);
        tv_title_1.setText("性别");
        if ("男".equals(sex)) {
            iv_man.setImageResource(R.drawable.icon_sex_select_sure);
        } else if ("女".equals(sex)) {
            iv_woman.setImageResource(R.drawable.icon_sex_select_sure);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_title_left:
                onBackPressed();
                break;
            case R.id.ll_man:
                sendData(SEX_MAN);
                break;
            case R.id.ll_woman:
                sendData(SEX_WOMAN);
                break;
        }
    }

    private void sendData(int sex) {
        Intent intent = new Intent();
        if (sex == 1) {
            iv_man.setImageResource(R.drawable.icon_sex_select_sure);
            iv_woman.setImageResource(R.color.white);
            intent.putExtra("sex", SEX_MAN);
        } else {
            iv_woman.setImageResource(R.drawable.icon_sex_select_sure);
            iv_man.setImageResource(R.color.white);
            intent.putExtra("sex", SEX_WOMAN);
        }
        setResult(RESULT_OK, intent);
        onBackPressed();
    }
}
