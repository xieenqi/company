package com.loyo.oa.v2.activityui.order.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.order.AddOrderActivity;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.tool.BaseFragment;

/**
 * 【我的订单】
 * Created by xeq on 16/8/1.
 */
public class MyOrderFragment extends BaseFragment implements View.OnClickListener {
    private Button btn_add;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mView = null;
        if (null == mView) {
            mView = inflater.inflate(R.layout.fragment_my_order, null);
            initView(mView);
        }
//        getData();
        return mView;
    }

    private void initView(View view) {
        btn_add = (Button) view.findViewById(R.id.btn_add);
        btn_add.setOnTouchListener(Global.GetTouch());
        btn_add.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add:
                Intent mIntent = new Intent();
                mIntent.setClass(getActivity(), AddOrderActivity.class);
                startActivityForResult(mIntent, getActivity().RESULT_FIRST_USER);
                getActivity().overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);
                break;
        }
    }
}
