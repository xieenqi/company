package com.loyo.oa.v2.activityui.customer;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.tool.BaseActivity;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 【电话回拨】等待界面
 * Created by yyy on 16/9/18.
 */
public class CallPhoneBackActivity extends BaseActivity implements View.OnClickListener{

    private ImageView iv_1, iv_2, iv_3, iv_4;
    private ImageView iv_call_gua;
    private TextView  tv_call_loading;
    private Animation anim1, anim2, anim3, anim4;

    private int tag = 0;

    private Handler handler = new Handler() {

        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            switch (msg.what) {
                case 1:
                    iv_2.setVisibility(View.VISIBLE);
                    iv_2.startAnimation(anim2);
                    break;
                case 2:
                    iv_3.setVisibility(View.VISIBLE);
                    iv_3.startAnimation(anim3);
                    break;
                case 3:
                    iv_4.setVisibility(View.VISIBLE);
                    iv_4.startAnimation(anim4);
                    break;
                case 4:
                    setAnim1();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_callphoneback);
        initUI();
    }

    private void initUI(){

        iv_1 = (ImageView) findViewById(R.id.iv_1);
        iv_2 = (ImageView) findViewById(R.id.iv_2);
        iv_3 = (ImageView) findViewById(R.id.iv_3);
        iv_4 = (ImageView) findViewById(R.id.iv_4);

        anim1 = AnimationUtils.loadAnimation(this, R.anim.call_phone_anim);
        anim2 = AnimationUtils.loadAnimation(this, R.anim.call_phone_anim);
        anim3 = AnimationUtils.loadAnimation(this, R.anim.call_phone_anim);
        anim4 = AnimationUtils.loadAnimation(this, R.anim.call_phone_anim);

        tv_call_loading = (TextView) findViewById(R.id.tv_call_loading);
        iv_call_gua = (ImageView) findViewById(R.id.iv_call_gua);
        iv_call_gua.setOnClickListener(this);
        iv_call_gua.setOnTouchListener(Global.GetTouch());

        setAnim2();
    }

    private void setAnim1(){
        switch (tag){
            case 1:
                tv_call_loading.setText(".");
                break;

            case 2:
                tv_call_loading.setText("..");
                break;

            case 3:
                tag = 0;
                tv_call_loading.setText("...");
                break;
        }
    }


    private void setAnim2() {

        Timer time = new Timer();

        iv_1.startAnimation(anim1);
        iv_1.setVisibility(View.VISIBLE);

        time.schedule(new TimerTask() {
            @Override
            public void run() {
                tag++;
                handler.sendEmptyMessage(4);
            }
        }, 0,1000);

        time.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(1);
            }
        }, 1000);

        time.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        handler.sendEmptyMessage(2);
                    }
                });
            }
        }, 2000);

        time.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(3);
            }
        }, 3000);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            /*挂断电话*/
            case R.id.iv_call_gua:
                finish();
                break;

        }
    }
}
