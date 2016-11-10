package com.loyo.oa.v2.activityui.dynamic;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.LogUtil;
/**
 * Created by yyy on 16/11/10.
 */

public class DynamicDetailsActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout layout_voice;
    private EditText edit_comment;
    private TextView tv_send_message;
    private ImageView iv_voice;
    private ScrollView layout_scrollview;

    private float mPosX;
    private float mPosY;
    private float mCurPosX;
    private float mCurPosY;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x01) {
                layout_voice.setVisibility(View.VISIBLE);
            } else if (msg.what == 0x02) {
                layout_voice.setVisibility(View.GONE);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic_details);
        initUI();
    }

    private void initUI() {
        layout_voice = (LinearLayout) findViewById(R.id.layout_voice);
        edit_comment = (EditText) findViewById(R.id.edit_comment);
        tv_send_message = (TextView) findViewById(R.id.tv_send_message);
        iv_voice = (ImageView) findViewById(R.id.iv_voice);
        layout_scrollview = (ScrollView) findViewById(R.id.layout_scrollview);

        iv_voice.setOnClickListener(this);
        tv_send_message.setOnClickListener(this);
        edit_comment.setOnClickListener(this);

        layout_scrollview.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    /*按下*/
                    case MotionEvent.ACTION_DOWN:
                        mPosX = event.getX();
                        mPosY = event.getY();
                        hideInputKeyboard(edit_comment);
                        layout_voice.setVisibility(View.GONE);
                        break;
                    /*移动*/
                    case MotionEvent.ACTION_MOVE:
                        mCurPosX = event.getX();
                        mCurPosY = event.getY();
                        break;
                    /*离开*/
                    case MotionEvent.ACTION_UP:
                        //向下滑動
                        if (mCurPosY - mPosY > 0 && (Math.abs(mCurPosY - mPosY) > 25)) {
                        }
                        //向上滑动
                        else if (mCurPosY - mPosY < 0 && (Math.abs(mCurPosY - mPosY) > 25)) {
                        }
                        break;
                }
                return true;
            }

        });

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            /*录音*/
            case R.id.iv_voice:

                hideInputKeyboard(edit_comment);
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        mHandler.sendEmptyMessage(0x01);
                    }

                }, 100);

                break;

            /*编辑*/
            case R.id.edit_comment:
                layout_voice.setVisibility(View.GONE);
                break;

            /*发送*/
            case R.id.tv_send_message:

                break;
        }
    }
}
