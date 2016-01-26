package com.loyo.oa.v2.activity.customer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.Customer;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.beans.SaleActivity;
import com.loyo.oa.v2.common.Global;
import com.loyo.oa.v2.common.http.HttpErrorCheck;
import com.loyo.oa.v2.point.ICustomer;
import com.loyo.oa.v2.tool.BaseActivity;
import com.loyo.oa.v2.tool.Config_project;
import com.loyo.oa.v2.tool.DateTool;
import com.loyo.oa.v2.tool.LogUtil;
import com.loyo.oa.v2.tool.RCallback;
import com.loyo.oa.v2.tool.RestAdapterFactory;
import com.loyo.oa.v2.tool.Utils;
import com.loyo.oa.v2.tool.ViewHolder;
import com.loyo.oa.v2.tool.ViewUtil;
import com.loyo.oa.v2.tool.customview.pullToRefresh.PullToRefreshBase;
import com.loyo.oa.v2.tool.customview.pullToRefresh.PullToRefreshListView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 跟进动态
 */
public class SaleActivitiesManageActivity extends BaseActivity implements View.OnClickListener, PullToRefreshBase.OnRefreshListener2 {

    public static final int ACTIVITIES_ADD = 101;

    ViewGroup img_title_left, layout_add;
    PullToRefreshListView lv_saleActivity;
    SaleActivitiesAdapter listAdapter;
    ArrayList<SaleActivity> lstData_saleActivity_current = new ArrayList<>();
    private PaginationX<SaleActivity> paginationX = new PaginationX<>(20);
    Customer customer;
    SaleActivity mSaleActivity;
    private boolean isChanged;
    private boolean isTopAdd = true;
    boolean isMyUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_activities_manage);


        if (getIntent() != null) {
            Bundle bundle = getIntent().getExtras();
            customer = (Customer) bundle.getSerializable(Customer.class.getName());
            isMyUser = bundle.getBoolean("isMyUser");
        }

        setTitle("跟进动态");
        initUI();
        getData();
    }

    /**
     * 获取数据
     */
    private void getData() {
        if (customer != null) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("pageIndex", paginationX.getPageIndex());
            map.put("pageSize", isTopAdd ? lstData_saleActivity_current.size() >= 20 ? lstData_saleActivity_current.size() : 20 : 20);
            RestAdapterFactory.getInstance().build(Config_project.API_URL_CUSTOMER()).create(ICustomer.class).getSaleactivity(customer.getId(), map, new RCallback<PaginationX<SaleActivity>>() {
                @Override
                public void success(PaginationX<SaleActivity> paginationXes, Response response) {

                    try {
                        LogUtil.dll("跟进动态数据:" + Utils.convertStreamToString(response.getBody().in()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    lv_saleActivity.onRefreshComplete();
                    if (!PaginationX.isEmpty(paginationXes)) {
                        paginationX = paginationXes;
                        if (isTopAdd) {
                            lstData_saleActivity_current.clear();
                        }
                        lstData_saleActivity_current.addAll(paginationX.getRecords());
                        bindData();
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    HttpErrorCheck.checkError(error);
                    lv_saleActivity.onRefreshComplete();
                    super.failure(error);
                }
            });
        }
    }

    private void initUI() {

        img_title_left = (ViewGroup) findViewById(R.id.img_title_left);
        img_title_left.setOnClickListener(this);
        img_title_left.setOnTouchListener(new ViewUtil.OnTouchListener_view_transparency());
        layout_add = (ViewGroup) findViewById(R.id.layout_add);
        if (!isMyUser) {
            layout_add.setVisibility(View.GONE);
        }
        layout_add.setOnTouchListener(Global.GetTouch());
        layout_add.setOnClickListener(this);
        lv_saleActivity = (PullToRefreshListView) findViewById(R.id.lv_saleActivity);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_title_left:
                onBackPressed();
                break;
            case R.id.layout_add:
                Bundle bundle = new Bundle();
                bundle.putSerializable(Customer.class.getName(), customer);
                app.startActivityForResult(this, SaleActivitiesAddActivity.class, MainApp.ENTER_TYPE_RIGHT, ACTIVITIES_ADD, bundle);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != RESULT_OK || data == null) {
            return;
        }
        switch (requestCode) {
            case ACTIVITIES_ADD:
                isChanged = true;
                SaleActivity activity = (SaleActivity) data.getSerializableExtra("data");
                if (activity != null) {
                    mSaleActivity = activity;
                    if (lstData_saleActivity_current != null) {
                        lstData_saleActivity_current.add(0, activity);
                    }
                    bindData();
                }
                break;
        }
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView) {
        isTopAdd = true;
        paginationX.setPageIndex(1);
        getData();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView) {
        isTopAdd = false;
        paginationX.setPageIndex(paginationX.getPageIndex() + 1);
        getData();
    }

    void bindData() {
        if (null == listAdapter) {
            listAdapter = new SaleActivitiesAdapter();
            lv_saleActivity.setAdapter(listAdapter);
            lv_saleActivity.setMode(PullToRefreshBase.Mode.BOTH);
            lv_saleActivity.setOnRefreshListener(this);
        } else {
            listAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed() {
        if (mSaleActivity != null) {
            Intent intent = new Intent();
            intent.putExtra("data", mSaleActivity);
            app.finishActivity(this, MainApp.ENTER_TYPE_LEFT, isChanged ? RESULT_OK : RESULT_CANCELED, intent);
            return;
        }

        super.onBackPressed();
    }


    private class SaleActivitiesAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return lstData_saleActivity_current.size();
        }

        @Override
        public Object getItem(int i) {
            return lstData_saleActivity_current.isEmpty() ? null : lstData_saleActivity_current.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_saleactivities_group_child, null);
            }

            TextView tv_previous = ViewHolder.get(convertView, R.id.tv_previous);
            TextView tv_creator_name = ViewHolder.get(convertView, R.id.tv_creator_name);
            TextView tv_content = ViewHolder.get(convertView, R.id.tv_content);
            TextView tv_time = ViewHolder.get(convertView, R.id.tv_time);
            TextView tv_timenow = ViewHolder.get(convertView, R.id.tv_timenow);
            ViewGroup layout_timenow = ViewHolder.get(convertView, R.id.layout_timenow);
            SaleActivity saleActivity = lstData_saleActivity_current.get(i);

            if (saleActivity.getRemindAt() != 0) {
                tv_timenow.setText(DateTool.timet(saleActivity.getRemindAt() + ""));
            }else{
                tv_timenow.setText("无");
            }

            tv_content.setText(saleActivity.getContent());
            tv_creator_name.setText(saleActivity.getCreator().getName());

            if (saleActivity.getType() != null) {
                tv_previous.setText(saleActivity.getType().getName());
            }
            tv_time.setText(DateTool.getDiffTime(saleActivity.getCreateAt() * 1000));
            if (i == lstData_saleActivity_current.size() - 1) {
                convertView.setBackgroundResource(R.drawable.item_bg_buttom);
            } else {
                convertView.setBackgroundColor(getResources().getColor(R.color.white));
            }
            return convertView;

        }
    }
}
