package com.loyo.oa.v2.customermanagement.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.pulltorefresh.PullToRefreshBase;
import com.loyo.oa.pulltorefresh.PullToRefreshListView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.customer.model.Customer;
import com.loyo.oa.v2.activityui.sale.AddMySaleActivity;
import com.loyo.oa.v2.activityui.sale.SaleDetailsActivity;
import com.loyo.oa.v2.activityui.sale.api.SaleService;
import com.loyo.oa.v2.activityui.sale.bean.SaleRecord;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.permission.BusinessOperation;
import com.loyo.oa.v2.permission.CustomerAction;
import com.loyo.oa.v2.permission.PermissionManager;
import com.loyo.oa.v2.tool.BaseMainListFragment;
import com.loyo.oa.v2.tool.ViewUtil;

import java.util.ArrayList;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;

/**
 * Created by EthanGong on 2017/2/9.
 */

public class OpportunitiesFragment extends CustomerChildFragment
        implements View.OnClickListener, PullToRefreshBase.OnRefreshListener2
{

    View view;
    public LoadingLayout ll_loading;
    private ViewGroup layout_add;
    private TextView tv_add;
    private PullToRefreshListView listView_demands;
    private AdapterCustomerSale listAdapter;
    private ArrayList<SaleRecord> listData = new ArrayList<>();
    private String customerId, customerName;
    private boolean canAdd;
    private boolean isChanged;
    private boolean isTopAdd = true;
    public static final int VIEW_DEMANDS = 200;
    public static final int CREATE_DEMANDS = 300;
    private int page = 1;

    public OpportunitiesFragment() {
        this.title = "机会";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_opptunities, container, false);

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

    public void getPageData() {
        isTopAdd = true;
        page = 1;
        getData();
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
        customerId = customer.getId();
        customerName = customer.name;
        canAdd = customer != null &&
                PermissionManager.getInstance().hasCustomerAuthority(
                        customer.relationState,
                        customer.state,
                        CustomerAction.SALE_OPPORTUNITY_ADD);
        this.totalCount = customer.counter.getDemand();
    }

    void initUI() {
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
        tv_add.setText("新增销售机会");
        layout_add.setVisibility(canAdd ? View.VISIBLE : View.GONE);
        layout_add.setOnClickListener(this);
        layout_add.setOnTouchListener(new ViewUtil.OnTouchListener_view_transparency());
        listView_demands = (PullToRefreshListView) view.findViewById(R.id.listView_demands);
    }

    @Override
    public void onResume() {
        super.onResume();
        isTopAdd = true;
        page = 1;
        getData();
    }

    /**
     * 获取销售机会
     */
    private void getData() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("pageIndex", page);
        map.put("pageSize", 20);
        map.put("customerId", customerId);
        SaleService.getCustomerSale(map).subscribe(new DefaultLoyoSubscriber<PaginationX<SaleRecord>>(ll_loading) {
            @Override
            public void onNext(PaginationX<SaleRecord> resultData) {
                listView_demands.onRefreshComplete();
                OpportunitiesFragment.this.totalCount = resultData.totalRecords;
                notifyTotalCountChange();
                if (null != resultData && !(resultData.getRecords().size() < 0)) {
                    if (isTopAdd) {
                        listData.clear();
                    }
                    listData.addAll(resultData.getRecords());
                    bindData();
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                listView_demands.onRefreshComplete();
            }
        });
    }

    /**
     * 绑定数据
     */
    private void bindData() {
        if (listData != null) {
            listAdapter = new AdapterCustomerSale(getActivity(), listData);
            listView_demands.setAdapter(listAdapter);
            listView_demands.setMode(PullToRefreshBase.Mode.BOTH);
            listView_demands.setOnRefreshListener(this);
        }
        ll_loading.setStatus(LoadingLayout.Success);
        if (isTopAdd && listData.size() == 0)
            ll_loading.setStatus(LoadingLayout.Empty);
    }

    @Override
    public void onClick(final View v) {
        Bundle bundle;
        switch (v.getId()) {
            case R.id.layout_add:

                if (!PermissionManager.getInstance().hasPermission(BusinessOperation.SALE_OPPORTUNITY)) {

                    sweetAlertDialogView.alertIcon(null, "此功能权限已关闭\n请联系管理员开启后再试!");

                } else {
                    if (customerId == null) {
                        break;
                    }
                    bundle = new Bundle();
                    bundle.putString(ExtraAndResult.EXTRA_NAME, customerName);
                    bundle.putString(ExtraAndResult.EXTRA_ID, customerId);
                    app.startActivityForResult(getActivity(), AddMySaleActivity.class,
                            MainApp.ENTER_TYPE_RIGHT, CREATE_DEMANDS, bundle);
                }

                break;

            default:
                break;
        }
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {

        if (resultCode != RESULT_OK || data == null) {
            return;
        }

        switch (requestCode) {
            case CREATE_DEMANDS:
            case VIEW_DEMANDS:
            case BaseMainListFragment.REQUEST_REVIEW:
                isChanged = true;
                onPullDownToRefresh(listView_demands);
                break;
            default:

                break;
        }
    }

    @Override
    public void onPullDownToRefresh(final PullToRefreshBase refreshView) {
        isTopAdd = true;
        page = 1;
        getData();
    }

    @Override
    public void onPullUpToRefresh(final PullToRefreshBase refreshView) {
        isTopAdd = false;
        page++;
        getData();
    }


    public class AdapterCustomerSale extends BaseAdapter {

        private ArrayList<SaleRecord> mData;
        private Context mContext;

        public AdapterCustomerSale(Context context, ArrayList<SaleRecord> data) {
            mContext = context;
            mData = data;
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Object getItem(final int i) {
            return mData.isEmpty() ? null : mData.get(i);
        }

        @Override
        public long getItemId(final int i) {
            return i;
        }

        @Override
        public View getView(final int position, View view, final ViewGroup viewGroup) {
            AdapterCustomerSale.HolderView holder;
            final SaleRecord record = mData.get(position);
            if (null == view) {
                view = LayoutInflater.from(mContext).inflate(R.layout.item_my_sale, viewGroup, false);
                holder = new AdapterCustomerSale.HolderView();
                holder.tv_name = (TextView) view.findViewById(R.id.tv_name);
                holder.tv_number = (TextView) view.findViewById(R.id.tv_number);
                holder.tv_price = (TextView) view.findViewById(R.id.tv_price);
                view.setTag(holder);
            } else {
                holder = (AdapterCustomerSale.HolderView) view.getTag();
            }
            holder.tv_name.setText(record.getName());
            holder.tv_number.setText(record.getStageNmae() + "(" + record.getProb() + "%)");
            holder.tv_price.setText("预估销售金额:" + record.getEstimatedAmount());
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!PermissionManager.getInstance().hasPermission(BusinessOperation.SALE_OPPORTUNITY)) {
                        sweetAlertDialogView.alertIcon(null, "此功能权限已关闭\n请联系管理员开启后再试!");
                        return;
                    }
                    Intent mIntent = new Intent();
                    mIntent.putExtra("id", record.getId());
                    mIntent.setClass(getActivity(), SaleDetailsActivity.class);
                    startActivityForResult(mIntent, ExtraAndResult.REQUEST_CODE);
                    getActivity().overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);
                }
            });
            return view;
        }

        class HolderView {
            TextView tv_name;
            TextView tv_number;
            TextView tv_price;
        }
    }
}
