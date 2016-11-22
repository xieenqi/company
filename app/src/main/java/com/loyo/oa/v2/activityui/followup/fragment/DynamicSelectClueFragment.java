package com.loyo.oa.v2.activityui.followup.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.clue.ClueSearchActivity;
import com.loyo.oa.v2.activityui.clue.ClueTypeEnum;
import com.loyo.oa.v2.activityui.clue.adapter.MyClueAdapter;
import com.loyo.oa.v2.activityui.clue.bean.ClueListItem;
import com.loyo.oa.v2.activityui.followup.DynamicAddActivity;
import com.loyo.oa.v2.activityui.followup.persenter.DynamicSelectCustomerAndCuleFragmentPCersener;
import com.loyo.oa.v2.activityui.followup.viewcontrol.DynamicSelectCustomerAndCuleFragmentVControl;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Customer;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.pulltorefresh.PullToRefreshBase;
import com.loyo.oa.pulltorefresh.PullToRefreshListView;
import com.loyo.oa.v2.tool.BaseFragment;

import java.util.ArrayList;

/**
 * 写跟进 选择 【线索】
 * Created by xeq on 16/11/10.
 */

public class DynamicSelectClueFragment extends BaseFragment implements DynamicSelectCustomerAndCuleFragmentVControl, PullToRefreshBase.OnRefreshListener2 {

    private LinearLayout ll_search;
    private PullToRefreshListView lv_list;
    private DynamicSelectCustomerAndCuleFragmentPCersener pCersener;
    private MyClueAdapter adapter;

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
        pCersener = new DynamicSelectCustomerAndCuleFragmentPCersener(this, DynamicSelectCustomerAndCuleFragmentPCersener.SELECT_CULE);
        ll_search = (LinearLayout) view.findViewById(R.id.ll_search);
        lv_list = (PullToRefreshListView) view.findViewById(R.id.lv_list);
        Global.SetTouchView(ll_search);
        pCersener.getPageData();
        lv_list.setMode(PullToRefreshBase.Mode.BOTH);
        lv_list.setOnRefreshListener(this);
        ll_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putInt(ExtraAndResult.EXTRA_TYPE, ClueTypeEnum.myCule.getType());
                b.putBoolean("isSelect", true);
                app.startActivity(mActivity, ClueSearchActivity.class, MainApp.ENTER_TYPE_RIGHT, false, b);
            }
        });
        adapter = new MyClueAdapter(mActivity);
        lv_list.setAdapter(adapter);
    }

    @Override
    public void showProgress(String message) {
    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void showMsg(String message) {
        Toast(message);
    }

    @Override
    public void bindCustomerData(ArrayList<Customer> mCustomers) {

    }

    @Override
    public void bindClueData(ArrayList<ClueListItem> mClues) {
        adapter.setData(mClues);
        lv_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent();
                intent.putExtra(ClueListItem.class.getName(), adapter.getItemData(position-1));
                intent.putExtra(ExtraAndResult.DYNAMIC_ADD_ACTION, ExtraAndResult.DYNAMIC_ADD_CULE);
                intent.setClass(mActivity, DynamicAddActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);
                mActivity.finish();
            }
        });
    }

    @Override
    public void getDataComplete() {
        lv_list.onRefreshComplete();
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        pCersener.pullDownCule();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        pCersener.pullUpCule();
    }
}
