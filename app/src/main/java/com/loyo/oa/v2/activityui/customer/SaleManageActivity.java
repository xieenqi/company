package com.loyo.oa.v2.activityui.customer;

import android.app.Activity;
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
import com.loyo.oa.v2.activityui.sale.AddMySaleActivity;
import com.loyo.oa.v2.activityui.sale.SaleDetailsActivity;
import com.loyo.oa.v2.activityui.sale.api.SaleService;
import com.loyo.oa.v2.activityui.sale.bean.SaleRecord;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.permission.BusinessOperation;
import com.loyo.oa.v2.permission.PermissionManager;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.BaseLoadingActivity;
import com.loyo.oa.v2.tool.BaseMainListFragment;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.ViewUtil;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【销售机会】客户详情中新增
 */

public class SaleManageActivity extends BaseLoadingActivity implements View.OnClickListener, PullToRefreshBase.OnRefreshListener2 {

    private ViewGroup img_title_left, layout_add;
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

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent() != null) {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                canAdd = bundle.getBoolean("canAdd");
                customerId = bundle.getString(ExtraAndResult.EXTRA_ID);
                customerName = bundle.getString(ExtraAndResult.EXTRA_NAME);
            }
        }
        super.setTitle("销售机会");
        initUI();

    }

    @Override
    public void setLayoutView() {
        setContentView(R.layout.activity_demands_manage);
    }

    @Override
    public void getPageData() {
        isTopAdd = true;
        page = 1;
        getData();
    }

    void initUI() {
        img_title_left = (ViewGroup) findViewById(R.id.img_title_left);
        layout_add = (ViewGroup) findViewById(R.id.layout_add);
        tv_add = (TextView) findViewById(R.id.tv_add);
        tv_add.setText("新增销售机会");
        layout_add.setVisibility(canAdd ? View.VISIBLE : View.GONE);
        img_title_left.setOnClickListener(this);
        img_title_left.setOnTouchListener(new ViewUtil.OnTouchListener_view_transparency());
        layout_add.setOnClickListener(this);
        layout_add.setOnTouchListener(new ViewUtil.OnTouchListener_view_transparency());
        listView_demands = (PullToRefreshListView) findViewById(R.id.listView_demands);
    }

    @Override
    protected void onResume() {
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
//        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ISale.class).
//                getCustomerSale(map, new RCallback<PaginationX<SaleRecord>>() {
//                    @Override
//                    public void success(PaginationX<SaleRecord> resultData, Response response) {
//                        HttpErrorCheck.checkResponse(" 客户销售机会列表：", response);
//                        listView_demands.onRefreshComplete();
//                        if (null != resultData && !(resultData.getRecords().size() < 0)) {
//                            if (isTopAdd) {
//                                listData.clear();
//                            }
//                            listData.addAll(resultData.getRecords());
//                            bindData();
//                        }
//                    }
//
//                    @Override
//                    public void failure(RetrofitError error) {
//                        HttpErrorCheck.checkError(error, ll_loading);
//                        listView_demands.onRefreshComplete();
//                        super.failure(error);
//                    }
//                });

        SaleService.getCustomerSale(map).subscribe(new DefaultLoyoSubscriber<PaginationX<SaleRecord>>(ll_loading) {
            @Override
            public void onNext(PaginationX<SaleRecord> resultData) {
                listView_demands.onRefreshComplete();
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
            listAdapter = new AdapterCustomerSale(this, listData);
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
            case R.id.img_title_left:
                onBackPressed();
                break;
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
                    app.startActivityForResult(this, AddMySaleActivity.class, MainApp.ENTER_TYPE_RIGHT, CREATE_DEMANDS, bundle);
                }

                break;

            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        app.finishActivity(this, MainApp.ENTER_TYPE_LEFT, isChanged ? RESULT_OK : RESULT_CANCELED, new Intent());
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
            HolderView holder;
            final SaleRecord record = mData.get(position);
            if (null == view) {
                view = LayoutInflater.from(mContext).inflate(R.layout.item_my_sale, viewGroup, false);
                holder = new HolderView();
                holder.tv_name = (TextView) view.findViewById(R.id.tv_name);
                holder.tv_number = (TextView) view.findViewById(R.id.tv_number);
                holder.tv_price = (TextView) view.findViewById(R.id.tv_price);
                view.setTag(holder);
            } else {
                holder = (HolderView) view.getTag();
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
                    mIntent.setClass(SaleManageActivity.this, SaleDetailsActivity.class);
                    startActivityForResult(mIntent, ExtraAndResult.REQUEST_CODE);
                    overridePendingTransition(R.anim.enter_righttoleft, R.anim.exit_righttoleft);
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
