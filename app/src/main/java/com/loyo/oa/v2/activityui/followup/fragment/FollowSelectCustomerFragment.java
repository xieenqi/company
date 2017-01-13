package com.loyo.oa.v2.activityui.followup.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.hud.progress.LoyoProgressHUD;
import com.loyo.oa.hud.toast.LoyoToast;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.clue.model.ClueListItem;
import com.loyo.oa.v2.activityui.followup.FollowAddActivity;
import com.loyo.oa.v2.activityui.customer.CustomerManagerActivity;
import com.loyo.oa.v2.activityui.customer.CustomerSearchActivity;
import com.loyo.oa.v2.activityui.customer.adapter.MyCustomerAdapter;
import com.loyo.oa.v2.activityui.followup.persenter.FollowSelectCustomerAndCuleFragmentPCersener;
import com.loyo.oa.v2.activityui.followup.viewcontrol.FollowSelectCustomerAndCuleFragmentVControl;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.activityui.customer.model.Customer;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.pulltorefresh.PullToRefreshBase;
import com.loyo.oa.pulltorefresh.PullToRefreshListView;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.BaseFragment;

import java.util.ArrayList;

/**
 * 写跟进 选择 【客户】
 * Created by xeq on 16/11/10.
 */

public class FollowSelectCustomerFragment extends BaseFragment implements FollowSelectCustomerAndCuleFragmentVControl, PullToRefreshBase.OnRefreshListener2 {
    private LinearLayout ll_search;
    private PullToRefreshListView lv_list;
    private FollowSelectCustomerAndCuleFragmentPCersener pCersener;
    private MyCustomerAdapter adapter;
    private LoadingLayout ll_loading;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_cule, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        ll_loading = (LoadingLayout) view.findViewById(R.id.ll_loading);
        ll_loading.setStatus(LoadingLayout.Loading);
        ll_loading.setOnReloadListener(new LoadingLayout.OnReloadListener() {
            @Override
            public void onReload(View v) {
                ll_loading.setStatus(LoadingLayout.Loading);
                pCersener.getPageData();
            }
        });
        pCersener = new FollowSelectCustomerAndCuleFragmentPCersener(this, FollowSelectCustomerAndCuleFragmentPCersener.SELECT_CUSTOMER);
        ll_search = (LinearLayout) view.findViewById(R.id.ll_search);
        lv_list = (PullToRefreshListView) view.findViewById(R.id.lv_list);
        lv_list.setMode(PullToRefreshBase.Mode.BOTH);
        lv_list.setOnRefreshListener(this);
        adapter = new MyCustomerAdapter(mActivity);
        lv_list.setAdapter(adapter);
        Global.SetTouchView(ll_search);
        pCersener.getPageData();
        ll_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putInt(ExtraAndResult.EXTRA_TYPE, CustomerManagerActivity.CUSTOMER_MY);
                b.putInt("from", BaseActivity.DYNAMIC_MANAGE);
                app.startActivity(mActivity, CustomerSearchActivity.class, MainApp.ENTER_TYPE_RIGHT, false, b);
            }
        });
    }

    @Override
    public LoyoProgressHUD showStatusProgress() {
        showCommitLoading();
        return hud;
    }

    @Override
    public LoyoProgressHUD showProgress(String message) {
        showLoading2("");
        return hud;
    }

    @Override
    public void hideProgress() {
        cancelLoading2();
        lv_list.onRefreshComplete();
    }

    @Override
    public void showMsg(String message) {
        LoyoToast.info(this.getActivity(), message);
    }


    /**
     * 绑定数据
     */
    @Override
    public void bindCustomerData(ArrayList<Customer> mCustomers) {
        adapter.setData(mCustomers);
        lv_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent mIntent = new Intent(mActivity, FollowAddActivity.class);
                mIntent.putExtra(ExtraAndResult.DYNAMIC_ADD_ACTION, ExtraAndResult.DYNAMIC_ADD_CUSTOMER);
                mIntent.putExtra(Customer.class.getName(), adapter.getItemData(position - 1));
                startActivity(mIntent);
                getActivity().overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);
                mActivity.finish();
            }
        });
    }

    @Override
    public void bindClueData(ArrayList<ClueListItem> mClues) {

    }

    @Override
    public void getDataComplete() {
        lv_list.onRefreshComplete();
    }

    @Override
    public LoadingLayout getLoadingLayout() {
        return ll_loading;
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        pCersener.pullDownCus();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        pCersener.pullUpCus();
    }
}
