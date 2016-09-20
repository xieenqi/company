package com.loyo.oa.v2.customview;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.common.Global;

/**
 * 【拨打电话】弹出框
 * Created by yyy on 16/9/19.
 */
public class CallPhonePopView extends Dialog {

    private TextView tv_title,tv_business,tv_commonly,tv_cancel;
    private String   title;

    public CallPhonePopView(Context context,String title) {
        super(context);
        this.title = title;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_callphone);
        tv_cancel   = (TextView) findViewById(R.id.tv_cancel);
        tv_title    = (TextView) findViewById(R.id.tv_title);
        tv_business = (TextView) findViewById(R.id.tv_business);
        tv_commonly = (TextView) findViewById(R.id.tv_commonly);

        tv_commonly.setOnTouchListener(Global.GetTouch());
        tv_business.setOnTouchListener(Global.GetTouch());
        tv_cancel.setOnTouchListener(Global.GetTouch());

        setTitle();
    }

    public void setTitle(){
        tv_title.setText(title);
    }

    /**
     * 商务电话
     * */
    public CallPhonePopView businessPhone(View.OnClickListener onClickListener){
        tv_business.setOnClickListener(onClickListener);
        return this;
    }

    /**
     * 普通电话电话
     * */
    public CallPhonePopView commonlyPhone(View.OnClickListener onClickListener){
        tv_commonly.setOnClickListener(onClickListener);
        return this;
    }

    /**
     * 取消
     * */
    public CallPhonePopView cancelPhone(View.OnClickListener onClickListener){
        tv_cancel.setOnClickListener(onClickListener);
        return this;
    }
}
