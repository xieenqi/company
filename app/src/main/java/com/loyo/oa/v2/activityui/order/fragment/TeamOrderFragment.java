package com.loyo.oa.v2.activityui.order.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.tool.BaseFragment;

/**
 * 【团队订单】
 * Created by xeq on 16/8/1.
 */
public class TeamOrderFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mView = null;
        if (null == mView) {
            mView = inflater.inflate(R.layout.fragment_team_order, null);
            initView(mView);
        }
//        getData();
        return mView;
    }

    private void initView(View view) {

    }
}
