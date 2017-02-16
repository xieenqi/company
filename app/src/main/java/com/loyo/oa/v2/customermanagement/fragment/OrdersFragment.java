package com.loyo.oa.v2.customermanagement.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.pulltorefresh.PullToRefreshBase;
import com.loyo.oa.pulltorefresh.PullToRefreshListView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.customer.model.Customer;
import com.loyo.oa.v2.activityui.order.OrderAddActivity;
import com.loyo.oa.v2.activityui.order.OrderDetailActivity;
import com.loyo.oa.v2.activityui.order.bean.OrderListItem;
import com.loyo.oa.v2.activityui.order.common.OrderCommon;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.customermanagement.api.CustomerService;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.network.LoyoErrorChecker;
import com.loyo.oa.v2.permission.BusinessOperation;
import com.loyo.oa.v2.permission.CustomerAction;
import com.loyo.oa.v2.permission.PermissionManager;
import com.loyo.oa.v2.tool.Utils;
import com.loyo.oa.v2.tool.ViewUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by EthanGong on 2017/2/9.
 */

public class OrdersFragment extends CustomerChildFragment
        implements View.OnClickListener, PullToRefreshBase.OnRefreshListener2
{

    View view;
    public LoadingLayout ll_loading;
    private ViewGroup layout_add;
    private TextView tv_add;
    private PullToRefreshListView listView_demands;
    private CustomerOrderAdapter listAdapter;
    private ArrayList<OrderListItem> listData = new ArrayList<>();
    private String customerId, customerName;
    private boolean canAdd;
    private int page = 1;
    private boolean isPullDown = true;

    public OrdersFragment() {
        this.title = "订单";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_orders, container, false);

            Bundle bundle = getArguments();
            if (bundle != null) {
                canAdd = bundle.getBoolean("canAdd");
                customerId = bundle.getString(ExtraAndResult.EXTRA_ID);
                customerName = bundle.getString(ExtraAndResult.EXTRA_NAME);
            }
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
                        CustomerAction.ORDER_ADD);
        this.totalCount = customer.counter.order;
    }

    public void reloadWithCustomer(Customer customer) {
        this.customer = customer;
        customerId = customer.getId();
        customerName = customer.name;
        canAdd = customer != null &&
                PermissionManager.getInstance().hasCustomerAuthority(
                        customer.relationState,
                        customer.state,
                        CustomerAction.ORDER_ADD);
        this.totalCount = customer.counter.order;
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
        layout_add = (ViewGroup) view.findViewById(R.id.layout_add);
        tv_add = (TextView) view.findViewById(R.id.tv_add);
        layout_add.setVisibility(canAdd ? View.VISIBLE : View.GONE);
        layout_add.setOnClickListener(this);
        layout_add.setOnTouchListener(new ViewUtil.OnTouchListener_view_transparency());
        listView_demands = (PullToRefreshListView) view.findViewWithTag("listView_demands");
        listAdapter = new CustomerOrderAdapter(getActivity());
        listView_demands.setAdapter(listAdapter);
        listView_demands.setMode(PullToRefreshBase.Mode.BOTH);
        listView_demands.setOnRefreshListener(this);
        listView_demands.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!PermissionManager.getInstance()
                        .hasPermission(BusinessOperation.ORDER_MANAGEMENT)) {
                    sweetAlertDialogView.alertIcon(null, "此功能权限已关闭\n请联系管理员开启后再试!");
                    return;
                }
                Intent mIntent = new Intent();
                mIntent.putExtra(ExtraAndResult.EXTRA_ID, listAdapter.getItemData(position - 1).id);
                mIntent.setClass(getActivity(), OrderDetailActivity.class);
                startActivityForResult(mIntent, getActivity().RESULT_FIRST_USER);
                getActivity().overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);
            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.layout_add:
                onAddOrder();
                break;
        }

    }

    public void onAddOrder() {
        canAdd = customer != null &&
                PermissionManager.getInstance().hasCustomerAuthority(
                        customer.relationState,
                        customer.state,
                        CustomerAction.ORDER_ADD);
        if (!canAdd) {
            sweetAlertDialogView.alertIcon("提示", "你没有添加客户相关订单权限");
        }
        else if (!PermissionManager.getInstance()
                .hasPermission(BusinessOperation.ORDER_MANAGEMENT)) {
            sweetAlertDialogView.alertIcon(null, "此功能权限已关闭\n请联系管理员开启后再试!");
        } else {
            if (customerId != null) {
                Bundle bundle;
                bundle = new Bundle();
                bundle.putString(ExtraAndResult.EXTRA_NAME, customerName);
                bundle.putString(ExtraAndResult.EXTRA_ID, customerId);
                bundle.putInt("fromPage", OrderDetailActivity.ORDER_ADD);
                app.startActivityForResult(getActivity(), OrderAddActivity.class,
                        MainApp.ENTER_TYPE_RIGHT, 200, bundle);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        isPullDown = true;
        page = 1;
        getData();
        notifyDropRemindRefresh();
    }

    /**
     * 获取 订单 列表
     */
    private void getData() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("pageIndex", page);
        map.put("pageSize", 15);
        CustomerService.getCutomerOrder(customerId, map)
                .subscribe(new DefaultLoyoSubscriber<PaginationX<OrderListItem>>() {
                    @Override
                    public void onError(Throwable e) {
                        /* 重写父类方法，不调用super */
                        @LoyoErrorChecker.CheckType
                        int type = listData.size() > 0 ?
                                LoyoErrorChecker.TOAST : LoyoErrorChecker.LOADING_LAYOUT;
                        LoyoErrorChecker.checkLoyoError(e, type, ll_loading);
                        listView_demands.onRefreshComplete();
                    }

                    @Override
                    public void onNext(PaginationX<OrderListItem> orderListItemPaginationX) {
                        listView_demands.onRefreshComplete();
                        ll_loading.setStatus(LoadingLayout.Success);
                        OrdersFragment.this.totalCount = orderListItemPaginationX.totalRecords;
                        notifyTotalCountChange();
                        if (! PaginationX.isEmpty(orderListItemPaginationX)) {
                            if (!isPullDown) {
                                listData.addAll(orderListItemPaginationX.records);
                            } else {
                                listData = orderListItemPaginationX.records;
                            }
                        }

                        listAdapter.setData(listData);
                        if (listData.size() == 0)
                            ll_loading.setStatus(LoadingLayout.Empty);
                    }
                });
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

    public class CustomerOrderAdapter extends BaseAdapter {

        Context context;
        LayoutInflater inflater;
        List<OrderListItem> data;

        public CustomerOrderAdapter(Context context) {
            this.context = context;
            inflater = LayoutInflater.from(context);
        }

        public void setData(List<OrderListItem> records) {
            this.data = records;
            notifyDataSetChanged();
        }

        public OrderListItem getItemData(int index) {
            return data.get(index);
        }

        @Override
        public int getCount() {
            return null == data ? 0 : data.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CustomerOrderAdapter.Holder holder = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_order_my_team, null);
                holder = new CustomerOrderAdapter.Holder();
                holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
                holder.tv_status = (TextView) convertView.findViewById(R.id.tv_status);
                holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                holder.tv_money = (TextView) convertView.findViewById(R.id.tv_money);
                holder.tv_customer = (TextView) convertView.findViewById(R.id.tv_customer);
                holder.tv_product = (TextView) convertView.findViewById(R.id.tv_product);
                holder.ll_customer = (LinearLayout) convertView.findViewById(R.id.ll_customer);
                holder.ll_responsible = (LinearLayout) convertView.findViewById(R.id.ll_responsible);
                convertView.setTag(holder);
            } else {
                holder = (CustomerOrderAdapter.Holder) convertView.getTag();
            }
            OrderListItem mData = data.get(position);
            holder.tv_title.setText(mData.title);
            OrderCommon.getOrderDetailsStatus(holder.tv_status, mData.status);
            holder.tv_money.setText(Utils.setValueDouble(mData.dealMoney));
            holder.ll_customer.setVisibility(View.GONE);
            holder.ll_responsible.setVisibility(View.VISIBLE);
            holder.tv_name.setText(mData.directorName);

            holder.tv_product.setText(mData.proName);
            holder.tv_time.setText(com.loyo.oa.common.utils.DateTool.getDateTimeFriendly(Long.valueOf(mData.createdAt + "")));
            return convertView;
        }

        class Holder {
            TextView tv_title, tv_status, tv_time, tv_name, tv_money, tv_customer, tv_product;
            LinearLayout ll_customer, ll_responsible;
        }
    }
}
