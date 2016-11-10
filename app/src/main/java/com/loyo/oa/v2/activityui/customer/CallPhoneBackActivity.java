package com.loyo.oa.v2.activityui.customer;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.customer.model.CallBackCallid;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.customview.SweetAlertDialogView;
import com.loyo.oa.v2.point.ICustomer;
import com.loyo.oa.v2.service.BroadcastReceiverMgr;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;

import java.util.Timer;
import java.util.TimerTask;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【电话回拨】等待界面
 * Created by yyy on 16/9/18.
 */
public class CallPhoneBackActivity extends BaseActivity implements View.OnClickListener,BroadcastReceiverMgr.onCallInBack{

    private ImageView iv_1, iv_2, iv_3, iv_4;
    private ImageView iv_call_gua;
    private TextView  tv_call_loading,tv_call_title,tv_call_name;
    private Animation anim1, anim2, anim3, anim4;

    private String callLogId;
    private String name;
    private int tag = 0;

    private BroadcastReceiverMgr mBroadcastReceiver;

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }

    private void initUI(){
        Intent mIntent = getIntent();
        name = mIntent.getStringExtra(ExtraAndResult.EXTRA_NAME);
        callLogId = mIntent.getStringExtra(ExtraAndResult.WELCOM_KEY);
        if(null == name || null == callLogId){
           Toast("参数不全");
            onBackPressed();
        }

        tv_call_title = (TextView) findViewById(R.id.tv_call_title);
        tv_call_name  = (TextView) findViewById(R.id.tv_call_name);

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

        if(TextUtils.isEmpty(name)){
            tv_call_name.setText("无姓名");
            tv_call_title.setText("无");
        }else{
            tv_call_name.setText(name);
            tv_call_title.setText(name.substring(0,1));
        }

        mBroadcastReceiver = new BroadcastReceiverMgr(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(mBroadcastReceiver.B_PHONE_STATE);
        intentFilter.setPriority(Integer.MAX_VALUE);
        registerReceiver(mBroadcastReceiver, intentFilter);

        setAnim2();
    }

    void requestCallBack2() {
        showLoading("");
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ICustomer.class).cancelCallBack(callLogId,
                new RCallback<CallBackCallid>() {
                    @Override
                    public void success(final CallBackCallid callBackCallid, final Response response) {
                        HttpErrorCheck.checkResponse("请求回拨", response);
                        if(callBackCallid.errcode == 0){
                            finish();
                        }else{
                            cancelLoading();
                            Toast(callBackCallid.errmsg);
                            finish();
                        }
                    }

                    @Override
                    public void failure(final RetrofitError error) {
                        super.failure(error);
                        HttpErrorCheck.checkError(error);
                    }
                });
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
                requestCallBack2();
                break;

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {

            sweetAlertDialogView = new SweetAlertDialogView(mContext);
            sweetAlertDialogView.alertHandle(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sweetAlertDialogView.sweetAlertDialog.dismiss();
                }
            }, new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    requestCallBack2();
                    sweetAlertDialogView.sweetAlertDialog.dismiss();
                }
            },"提示","电话正在回拨，是否取消?");

/*            final GeneralPopView generalPopView = showGeneralDialog(false,true,"电话正在回拨，是否取消?");
            generalPopView.setSureOnclick(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    requestCallBack2();
                    generalPopView.dismiss();
                }
            });*/

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void waitingCall(String phoneNumber) {
        LogUtil.dee("[Broadcast]电话呼入=" + phoneNumber);
        onBackPressed();
    }

    @Override
    public void onlineCall(String phoneNumber) {
        LogUtil.dee("[Broadcast]通话中=" + phoneNumber);
    }

    @Override
    public void cancelCall(String phoneNumber) {
        LogUtil.dee("[Broadcast]电话挂断=" + phoneNumber);

    }
}
