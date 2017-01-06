package com.loyo.oa.v2.activityui.dashboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.loyo.oa.v2.R;

/**
 * 仪表盘 特殊loading
 * Created by yyy on 16/12/28.
 */

public class DashboardLoadingView extends LinearLayout{

    View mView;
    LinearLayout onError;
    ImageView onLoading;

    public interface DashBdCallBack{
        void restRequestHttp();
    }

    public DashboardLoadingView(Context context,DashBdCallBack dashBdCallBack) {
        super(context);
        initUI(context,dashBdCallBack);
    }

    void initUI(Context mContext, final DashBdCallBack dashBdCallBack){
        mView = LayoutInflater.from(mContext).inflate(R.layout.dashboard_loading,null);
        addView(mView);

        onError = (LinearLayout) findViewById(R.id.layout_error);
        onLoading = (ImageView) findViewById(R.id.iv_loading);

        findViewById(R.id.tv_rest).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dashBdCallBack.restRequestHttp();
            }
        });
    }

    void setStatue(int type){

        switch (type){
            //成功
            case 0:
                onError.setVisibility(GONE);
                onLoading.setVisibility(GONE);
                break;

            //失败
            case 1:
                onLoading.setVisibility(GONE);
                onError.setVisibility(VISIBLE);
                break;

            case 2:
                onLoading.setVisibility(VISIBLE);
                onError.setVisibility(GONE);
                break;
        }
    }
}
