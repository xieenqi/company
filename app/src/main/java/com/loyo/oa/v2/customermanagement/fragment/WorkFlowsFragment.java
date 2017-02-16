package com.loyo.oa.v2.customermanagement.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.common.utils.DateTool;
import com.loyo.oa.pulltorefresh.PullToRefreshBase;
import com.loyo.oa.pulltorefresh.PullToRefreshExpandableListView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.customer.model.Customer;
import com.loyo.oa.v2.activityui.wfinstance.WfInTypeSelectActivity;
import com.loyo.oa.v2.activityui.wfinstance.WfinstanceInfoActivity_;
import com.loyo.oa.v2.activityui.wfinstance.bean.ApprovalItemModel;
import com.loyo.oa.v2.activityui.wfinstance.common.ApprovalAddBuilder;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.GroupsData;
import com.loyo.oa.v2.common.adapter.BaseGroupsDataAdapter;
import com.loyo.oa.v2.customermanagement.api.CustomerService;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.network.LoyoErrorChecker;
import com.loyo.oa.v2.permission.BusinessOperation;
import com.loyo.oa.v2.permission.CustomerAction;
import com.loyo.oa.v2.permission.PermissionManager;
import com.loyo.oa.v2.tool.ViewUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by EthanGong on 2017/2/9.
 */

public class WorkFlowsFragment extends CustomerChildFragment
        implements View.OnClickListener, PullToRefreshBase.OnRefreshListener2
{
    View view;
    private TextView tv_add;
    public LoadingLayout ll_loading;
    private ViewGroup layout_add;
    private ArrayList<ApprovalItemModel> listData = new ArrayList<>();
    private String customerId, customerName;
    private boolean canAdd;
    private int page = 1;
    private boolean isPullDown = true;

    protected GroupsData groupsData;
    CustomerRelatedApprovalAdapter adapter;
    protected PullToRefreshExpandableListView mExpandableListView;
    protected ExpandableListView expandableListView;

    public WorkFlowsFragment() {
        this.title = "审批";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_workflows, container, false);

            Bundle bundle = getArguments();
            if (bundle != null) {
                canAdd = bundle.getBoolean("canAdd");
                customerId = bundle.getString(ExtraAndResult.EXTRA_ID);
                customerName = bundle.getString(ExtraAndResult.EXTRA_NAME);
            }
            groupsData = new GroupsData();
            initUI();
        }
        return view;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
        customerId = customer.getId();
        customerName = customer.name;
        canAdd = customer != null &&
                PermissionManager.getInstance().hasCustomerAuthority(
                        customer.relationState,
                        customer.state,
                        CustomerAction.APPROVAL_ADD);
        this.totalCount = customer.counter.workflow;
    }

    public void reloadWithCustomer(Customer customer) {
        this.customer = customer;
        customerId = customer.getId();
        customerName = customer.name;
        canAdd = customer != null &&
                PermissionManager.getInstance().hasCustomerAuthority(
                        customer.relationState,
                        customer.state,
                        CustomerAction.APPROVAL_ADD);
        if (view == null) {
            return;
        }
        layout_add.setVisibility(canAdd ? View.VISIBLE : View.GONE);
    }

    public void getPageData() {
        isPullDown = true;
        page = 1;
        getData();
    }

    private void initUI() {
        ll_loading = (LoadingLayout) view.findViewById(R.id.ll_loading);
        ll_loading.setStatus(LoadingLayout.Loading);
        ll_loading.setOnReloadListener(new LoadingLayout.OnReloadListener() {
            @Override
            public void onReload(View v) {
                ll_loading.setStatus(LoadingLayout.Loading);
                getPageData();
            }
        });
        tv_add = (TextView) view.findViewById(R.id.tv_add);
        tv_add.setText("新建审批");
        layout_add = (ViewGroup) view.findViewById(R.id.layout_add);
        layout_add.setVisibility(canAdd ? View.VISIBLE : View.GONE);
        layout_add.setOnClickListener(this);
        layout_add.setOnTouchListener(new ViewUtil.OnTouchListener_view_transparency());

        mExpandableListView = (PullToRefreshExpandableListView)view.findViewWithTag("expandableListView");
        mExpandableListView.setOnRefreshListener(this);
        setupExpandableListView(
                new ExpandableListView.OnGroupClickListener() {
                    @Override
                    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                        return true;
                    }
                },
                new ExpandableListView.OnChildClickListener() {
                    @Override
                    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                        ApprovalItemModel item = (ApprovalItemModel) adapter.getChild(groupPosition, childPosition);
                        Intent intent = new Intent();
                        intent.putExtra(ExtraAndResult.EXTRA_ID, item.id);
                        if (!item.viewed) {//有红点需要刷新
                            intent.putExtra(ExtraAndResult.IS_UPDATE, true);
                        }
                        intent.setClass(getActivity(), WfinstanceInfoActivity_.class);
                        startActivityForResult(intent, ExtraAndResult.REQUEST_CODE);

                        return true;
                    }
                });
        initAdapter();
        expand();
        ll_loading.setStatus(LoadingLayout.Loading);
        getPageData();
    }

    @Override
    public void onClick(View v) {
        Bundle bundle;
        switch (v.getId()) {
            case R.id.layout_add:

                if (!PermissionManager.getInstance()
                        .hasPermission(BusinessOperation.ORDER_MANAGEMENT)) {
                    sweetAlertDialogView.alertIcon(null, "此功能权限已关闭\n请联系管理员开启后再试!");

                } else {
                    if (customerId == null) {
                        break;
                    }
                    ApprovalAddBuilder.startCreate();
                    ApprovalAddBuilder.setCustomer(customerId, customerName);
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), WfInTypeSelectActivity.class);
                    startActivityForResult(intent, ExtraAndResult.REQUEST_CODE);
                    getActivity().overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);
                }
                break;
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        isPullDown = true;
        page = 1;
        getData();
    }

    /**
     * 获取 审批 列表
     */
    private void getData() {

        HashMap<String, Object> map = new HashMap<>();
        map.put("pageIndex", page);
        map.put("pageSize", 20);
        map.put("status", "");
        map.put("bizformId", "");

        CustomerService.getRelatedApprovalList(customerId, map)
                .subscribe(new DefaultLoyoSubscriber<PaginationX<ApprovalItemModel>>(ll_loading) {
                    @Override
                    public void onError(Throwable e) {
                        mExpandableListView.onRefreshComplete();
                        @LoyoErrorChecker.CheckType
                        int type = listData.size()>0?LoyoErrorChecker.TOAST:LoyoErrorChecker.LOADING_LAYOUT;
                        LoyoErrorChecker.checkLoyoError(e, type, ll_loading);
                    }

                    @Override
                    public void onNext(PaginationX<ApprovalItemModel> x) {
                        mExpandableListView.onRefreshComplete();
                        WorkFlowsFragment.this.totalCount = x.totalRecords;
                        notifyTotalCountChange();
                        if (isPullDown) {
                            listData.clear();
                            groupsData.clear();
                        }


                        if (! PaginationX.isEmpty(x)) {
                            listData.addAll(x.records);
                            loadData(x.records);
                        }

                        if (listData.size() == 0) {
                            ll_loading.setStatus(LoadingLayout.Empty);
                        }
                        else {
                            ll_loading.setStatus(LoadingLayout.Success);
                            if (PaginationX.isEmpty(x)) {
                                Toast("没有更多了");
                            }
                        }
                    }
                });
    }

    private void loadData(List<ApprovalItemModel> list) {
        Iterator<ApprovalItemModel> iterator = list.iterator();
        while (iterator.hasNext()) {
            groupsData.addItem(iterator.next());
        }
        groupsData.sort();
        try {
            adapter.notifyDataSetChanged();
            expand();
        } catch (Exception e) {

        }
    }


    protected void expand() {
        for (int i = 0; i < groupsData.size(); i++) {
            mExpandableListView.getRefreshableView().expandGroup(i, false);//true 自动滑到底部
        }
    }

    public void initAdapter() {
        if (null == adapter) {
            adapter = new CustomerRelatedApprovalAdapter(getActivity(), groupsData);
            mExpandableListView.getRefreshableView().setAdapter(adapter);
        }
    }

    protected  void setupExpandableListView(ExpandableListView.OnGroupClickListener groupClickListener,
                                            ExpandableListView.OnChildClickListener childClickListener) {
        expandableListView = mExpandableListView.getRefreshableView();
        expandableListView.setOnGroupClickListener(groupClickListener);
        expandableListView.setOnChildClickListener(childClickListener);
    }


    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        isPullDown = true;
        page = 1;
        getData();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        isPullDown = false;
        page++;
        getData();
    }

    public class CustomerRelatedApprovalAdapter extends BaseGroupsDataAdapter {

        public CustomerRelatedApprovalAdapter(Context context, final GroupsData data) {
            this.mContext = context;
            this.groupsData = data;
        }
        @Override
        public View getChildView(final int groupPosition, final int childPosition, final boolean isLastChild, View convertView, final ViewGroup parent) {
            CustomerRelatedApprovalAdapter.ViewHolder holder = null;
            if (convertView == null) {
                holder = new CustomerRelatedApprovalAdapter.ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_approval_cell, null, false);
                holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
                holder.tv_approver = (TextView) convertView.findViewById(R.id.tv_approver);
                holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                convertView.setTag(holder);

            } else {
                holder = (CustomerRelatedApprovalAdapter.ViewHolder) convertView.getTag();
            }
            ApprovalItemModel model = (ApprovalItemModel) getChild(groupPosition, childPosition);
            holder.loadData(model);
            return convertView;
        }

        private class ViewHolder {

            TextView tv_title;
            TextView tv_approver;
            TextView tv_time;
            ApprovalItemModel model;

            public void loadData(ApprovalItemModel model) {
                this.model = model;
                tv_title.setText(model.title);
                tv_approver.setText(model.nextExecutorName);
                tv_time.setText(DateTool.getDateTimeFriendly(model.createdAt));
            }
        }
    }
}
