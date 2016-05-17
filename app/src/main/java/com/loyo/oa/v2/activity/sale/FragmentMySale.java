package com.loyo.oa.v2.activity.sale;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.customview.pullToRefresh.PullToRefreshListView;

/**
 * 【我的销售机会】
 * Created by xeq on 16/5/17.
 */
public class FragmentMySale extends BaseFragment {

    private View mView;
    private PullToRefreshListView lv_list;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == mView) {
            mView = inflater.inflate(R.layout.fragment_my_sale, null);
            initUi(mView);
        }
        return mView;
    }

    private void initUi(View myView) {
        lv_list = (PullToRefreshListView) myView.findViewById(R.id.lv_list);
        lv_list.setAdapter(new AdapterSaleList());
    }
}
