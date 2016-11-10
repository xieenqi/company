package com.loyo.oa.v2.activityui.contact;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loyo.oa.indexablelist.adapter.expand.LinearLayoutOrientationProvider;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.RegexUtil;

/**
 * 修改 【微信】号页面  页面简单不使用MVP
 * Created by xeq on 16/11/2.
 */

public class AlterWechatActivity extends BaseActivity implements View.OnClickListener {
    private ViewGroup img_title_left, img_title_right;
    private TextView tv_title_1;
    private EditText et_wechat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alter_wechat);
        initView();
    }

    private void initView() {
        String wechat = getIntent().getStringExtra("wechat");
        img_title_left = (LinearLayout) findViewById(R.id.img_title_left);
        img_title_right = (RelativeLayout) findViewById(R.id.img_title_right);
        tv_title_1 = (TextView) findViewById(R.id.tv_title_1);
        tv_title_1.setText("微信");
        et_wechat = (EditText) findViewById(R.id.et_wechat);
        img_title_left.setOnTouchListener(Global.GetTouch());
        img_title_left.setOnClickListener(this);
        img_title_right.setOnTouchListener(Global.GetTouch());
        img_title_right.setOnClickListener(this);
        et_wechat.setText(wechat);
        et_wechat.setSelection(et_wechat.length());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_title_left:
                onBackPressed();
                break;
            case R.id.img_title_right:
                String wechat = et_wechat.getText().toString();
                if (!wechat.isEmpty()) {
                    if (!RegexUtil.regexk(wechat, RegexUtil.StringType.WX)) {
                        Toast("微信号码不正确");
                        return;
                    }
                }
                Intent intent = new Intent();
                intent.putExtra("wechat", wechat);
                setResult(RESULT_OK, intent);
                onBackPressed();
                break;
        }
    }


}

