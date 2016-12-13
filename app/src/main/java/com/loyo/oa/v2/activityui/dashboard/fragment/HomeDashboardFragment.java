package com.loyo.oa.v2.activityui.dashboard.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.dashboard.adapter.StockListAdapter;
import com.loyo.oa.v2.activityui.dashboard.presenter.HomeDashboardPresenter;
import com.loyo.oa.v2.activityui.dashboard.presenter.impl.HomeDashboardPresenterImpl;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.customview.CustomerListView;
import com.loyo.oa.v2.tool.BaseFragment;
import me.itangqi.waveloadingview.WaveLoadingView;

/**
 * 【仪表盘】
 * Created by yyy on 16/12/9.
 */

public class HomeDashboardFragment extends BaseFragment implements View.OnClickListener{

    private View mView;
    private RadioButton rb_customer,rb_clue;
    private LinearLayout ll_dashboard_cus_followup;
    private LinearLayout ll_case1,ll_case2,ll_case3;
    private CustomerListView lv_stocklist;
    private StockListAdapter mAdapter;

    private HomeDashboardPresenter mPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(null == mView){
            mView = inflater.inflate(R.layout.fragment_dashboard, container, false);
        }
        initUI();
        return mView;
    }

    private void bindAdapter(){
        if(null == mAdapter){
            mAdapter = new StockListAdapter(getActivity());
            lv_stocklist.setAdapter(mAdapter);
        }else{
            mAdapter.notifyDataSetChanged();
        }
    }

    private void initUI(){

        mPresenter = new HomeDashboardPresenterImpl(getActivity());
        rb_customer = (RadioButton) mView.findViewById(R.id.rb_customer);
        rb_clue = (RadioButton)  mView.findViewById(R.id.rb_clue);
        lv_stocklist = (CustomerListView) mView.findViewById(R.id.lv_stocklist);
        ll_dashboard_cus_followup = (LinearLayout) mView.findViewById(R.id.ll_dashboard_cus_followup);
        ll_case1 = (LinearLayout) mView.findViewById(R.id.ll_case1);
        ll_case2 = (LinearLayout) mView.findViewById(R.id.ll_case2);
        ll_case3 = (LinearLayout) mView.findViewById(R.id.ll_case3);

        rb_customer.setOnClickListener(this);
        rb_clue.setOnClickListener(this);
        ll_case1.setOnClickListener(this);
        ll_case2.setOnClickListener(this);
        ll_case3.setOnClickListener(this);

        ll_case1.setOnTouchListener(Global.GetTouch());
        ll_case2.setOnTouchListener(Global.GetTouch());
        ll_case3.setOnTouchListener(Global.GetTouch());

        mPresenter.initUi((WaveLoadingView) mView.findViewById(R.id.waveLoadingView1),
                          (WaveLoadingView) mView.findViewById(R.id.waveLoadingView2));

        ll_dashboard_cus_followup.setOnClickListener(this);
        Global.SetTouchView(ll_dashboard_cus_followup);

        bindAdapter();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){

            /*客户跟进*/
            case R.id.rb_customer:
                Toast("1");
                break;

            /*线索跟进*/
            case R.id.rb_clue:
                Toast("2");
                break;

            /*客户线索筛选*/
            case R.id.ll_case1:

                break;

            /*增量存量筛选*/
            case R.id.ll_case2:

                break;

            /*数量金额筛选*/
            case R.id.ll_case3:

                break;
        }
    }
}
