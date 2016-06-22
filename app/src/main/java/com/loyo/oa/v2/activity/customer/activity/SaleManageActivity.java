package com.loyo.oa.v2.activity.customer.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activity.sale.ActivityAddMySale;
import com.loyo.oa.v2.activity.sale.ActivitySaleDetails;
import com.loyo.oa.v2.activity.sale.bean.SaleRecord;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.beans.Permission;
import com.loyo.oa.v2.common.ExtraAndResult;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.ISale;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.BaseMainListFragment;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.ViewUtil;
import com.loyo.oa.v2.tool.customview.GeneralPopView;
import com.loyo.oa.v2.tool.customview.pullToRefresh.PullToRefreshBase;
import com.loyo.oa.v2.tool.customview.pullToRefresh.PullToRefreshListView;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【销售机会】客户详情中新增
 */

public class SaleManageActivity extends BaseActivity implements View.OnClickListener, PullToRefreshBase.OnRefreshListener2 {

    private ViewGroup img_title_left, layout_add;
    private TextView tv_add;
    private Permission permission;
    private PullToRefreshListView listView_demands;
    private AdapterCustomerSale listAdapter;
    private ArrayList<SaleRecord> listData = new ArrayList<>();
    private String customerId, customerName;
    private boolean isMyUser;
    private boolean isChanged;
    private boolean isTopAdd = true;
    public static final int VIEW_DEMANDS = 200;
    public static final int CREATE_DEMANDS = 300;
    private int page = 1;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTouchView(NO_SCROLL);
        setContentView(R.layout.activity_demands_manage);
        if (getIntent() != null) {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                isMyUser = bundle.getBoolean("isMyUser");
                customerId = bundle.getString(ExtraAndResult.EXTRA_ID);
                customerName = bundle.getString(ExtraAndResult.EXTRA_NAME);
            }
        }
        super.setTitle("销售机会");
        initUI();

    }

    void initUI() {
        img_title_left = (ViewGroup) findViewById(R.id.img_title_left);
        layout_add = (ViewGroup) findViewById(R.id.layout_add);
        tv_add = (TextView) findViewById(R.id.tv_add);
        tv_add.setText("新增销售机会");
        if (!isMyUser) {
            layout_add.setVisibility(View.GONE);
        }
        img_title_left.setOnClickListener(this);
        img_title_left.setOnTouchListener(new ViewUtil.OnTouchListener_view_transparency());
        layout_add.setOnClickListener(this);
        layout_add.setOnTouchListener(new ViewUtil.OnTouchListener_view_transparency());
        listView_demands = (PullToRefreshListView) findViewById(R.id.listView_demands);

        //超级管理员\权限判断
        if (!MainApp.user.isSuperUser()) {
            try {
                permission = (Permission) MainApp.rootMap.get("0215");
            } catch (NullPointerException e) {
                e.printStackTrace();
                Toast("发布公告权限,code错误:0402");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        page = 1;
        getData();
    }

    /**
     * 获取销售机会
     */
    private void getData() {
        showLoading("");
        HashMap<String, Object> map = new HashMap<>();
        map.put("pageIndex", page);
        map.put("pageSize", 20);
        map.put("customerId", customerId);
        RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ISale.class).
                getCustomerSale(map, new RCallback<PaginationX<SaleRecord>>() {
                    @Override
                    public void success(PaginationX<SaleRecord> resultData, Response response) {
                        HttpErrorCheck.checkResponse(" 客户销售机会列表：", response);
                        listView_demands.onRefreshComplete();
                        if (null != resultData && resultData.getRecords().size() > 0) {
                            if (isTopAdd) {
                                listData.clear();
                            }
                            listData.addAll(resultData.getRecords());
                            bindData();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        HttpErrorCheck.checkError(error);
                        listView_demands.onRefreshComplete();
                        super.failure(error);
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
    }

    @Override
    public void onClick(final View v) {
        Bundle bundle;
        switch (v.getId()) {
            case R.id.img_title_left:
                onBackPressed();
                break;
            case R.id.layout_add:

                    if(null != permission && !permission.isEnable()){
                        showGeneralDialog(true, false, "此功能权限已关闭，请联系管理员开启后再试！")
                                .setNoCancelOnclick(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        generalPopView.dismiss();
                                    }
                                });

                    }else{
                        if (customerId == null) {
                            break;
                        }
                        bundle = new Bundle();
                        bundle.putString(ExtraAndResult.EXTRA_NAME, customerName);
                        bundle.putString(ExtraAndResult.EXTRA_ID, customerId);
                        app.startActivityForResult(this, ActivityAddMySale.class, MainApp.ENTER_TYPE_RIGHT, CREATE_DEMANDS, bundle);
                    }

                break;

            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        app.finishActivity((Activity) mContext, MainApp.ENTER_TYPE_LEFT, isChanged ? RESULT_OK : RESULT_CANCELED, new Intent());
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
                    Intent mIntent = new Intent();
                    mIntent.putExtra("id", record.getId());
                    mIntent.setClass(SaleManageActivity.this, ActivitySaleDetails.class);
                    startActivityForResult(mIntent, ExtraAndResult.REQUEST_CODE);
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
