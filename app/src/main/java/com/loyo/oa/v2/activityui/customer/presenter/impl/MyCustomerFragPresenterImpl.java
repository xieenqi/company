package com.loyo.oa.v2.activityui.customer.presenter.impl;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.customer.presenter.MyCustomerFragPresenter;
import com.loyo.oa.v2.activityui.customer.viewcontrol.MyCustomerFragView;
import com.loyo.oa.v2.common.Global;

/**
 * Created by yyy on 16/10/28.
 */

public class MyCustomerFragPresenterImpl implements MyCustomerFragPresenter{

    private Context mContext;
    private MyCustomerFragView crolView;

    public MyCustomerFragPresenterImpl(Context mContext, MyCustomerFragView crolView){
        this.mContext = mContext;
        this.crolView = crolView;
    }

    /**
     * 加载Pop添加客户弹窗
     * */
    @Override
    public void setInsertPopWindiw(Button btn_add) {
        View mView = LayoutInflater.from(mContext).inflate(R.layout.activity_contactlist_add, null);
        mView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

        final PopupWindow popupWindow = new PopupWindow(mContext);
        //popupWindow.setAnimationStyle(R.style.popwin_anim_style);
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setContentView(mView);
        popupWindow.setOutsideTouchable(false);
        popupWindow.setFocusable(true);
        ColorDrawable dw = new ColorDrawable(0x00000000);
        popupWindow.setBackgroundDrawable(dw);

        int[] location = new int[2];
        btn_add.getLocationOnScreen(location);

        int popupWidth = mView.getMeasuredWidth();
        int popupHeight =  mView.getMeasuredHeight();

        popupWindow.showAtLocation(btn_add, Gravity.NO_GRAVITY, (location[0]+btn_add.getWidth()/2)-popupWidth/2,
                location[1]-popupHeight);

        LinearLayout ll_autoinsert = (LinearLayout) mView.findViewById(R.id.ll_autoinsert);
        LinearLayout ll_handinsert = (LinearLayout) mView.findViewById(R.id.ll_handinsert);

        ll_autoinsert.setOnTouchListener(Global.GetTouch());
        ll_handinsert.setOnTouchListener(Global.GetTouch());

        /*手动添加*/
        ll_handinsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crolView.intentHandInsert(popupWindow);
            }
        });

        /*通讯录导入*/
        ll_autoinsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crolView.intentAutoInsert(popupWindow);
            }
        });
    }
}

