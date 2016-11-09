package com.loyo.oa.v2.activityui.dynamic.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.beans.Customer;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshBase;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshListView;
import com.loyo.oa.v2.tool.BaseFragment;
import com.loyo.oa.v2.tool.Utils;


/**
 * 【我参与的】列表
 * Created by yyy on 16/6/1.
 */
public class TeamDynamicFragment extends BaseFragment implements PullToRefreshBase.OnRefreshListener2 {

    private View mView;
    private Button btn_add;
    private ViewStub emptyView;
    private ViewGroup nearLayout;
    private PullToRefreshListView listView;
    private PaginationX<Customer> mPagination = new PaginationX<>(20);


    @SuppressLint("InflateParams")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == mView) {
            mView = inflater.inflate(R.layout.fragment_dynamic, null);
            initView(mView);
        }
        return mView;
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        mPagination.setPageIndex(1);
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        mPagination.setPageIndex(mPagination.getPageIndex() + 1);
    }

    public void initView(View view) {


        btn_add = (Button) view.findViewById(R.id.btn_add);
        emptyView = (ViewStub) mView.findViewById(R.id.vs_nodata);

        nearLayout = (ViewGroup) view.findViewById(R.id.layout_near_customers);

        listView = (PullToRefreshListView) view.findViewById(R.id.lv_list);
        listView.setEmptyView(emptyView);
        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listView.setOnRefreshListener(this);
        nearLayout.setOnClickListener(click);
        nearLayout.setOnTouchListener(Global.GetTouch());

        btn_add.setOnClickListener(click);
        btn_add.setOnTouchListener(Global.GetTouch());

        Utils.btnHideForListView(listView.getRefreshableView(), btn_add);
    }

    private View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {

                //新建跟进
                case R.id.btn_add:

                    break;
            }
        }
    };
}
