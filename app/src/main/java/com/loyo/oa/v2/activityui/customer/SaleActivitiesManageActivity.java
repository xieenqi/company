package com.loyo.oa.v2.activityui.customer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.customer.adapter.DynamicListnestingAdapter;
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
import com.loyo.oa.v2.tool.ViewHolder;
import com.loyo.oa.v2.tool.ViewUtil;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshBase;
import com.loyo.oa.v2.customview.pullToRefresh.PullToRefreshListView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 【跟进动态】 客户的
 */
public class SaleActivitiesManageActivity extends BaseActivity implements View.OnClickListener, PullToRefreshBase.OnRefreshListener2 {

    public static final int ACTIVITIES_ADD = 101;

    private ViewGroup img_title_left, layout_add;
    private PullToRefreshListView lv_saleActivity;
    private SaleActivitiesAdapter listAdapter;
    private DynamicListnestingAdapter  nestionListAdapter;
    private ArrayList<SaleActivity> lstData_saleActivity_current = new ArrayList<>();
    private PaginationX<SaleActivity> paginationX = new PaginationX<>(20);
    private Customer customer;
    private SaleActivity mSaleActivity;

    private boolean isChanged;
    private boolean isTopAdd = true;
    boolean isMyUser;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
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
                public void success(final PaginationX<SaleActivity> paginationXes, final Response response) {
                    HttpErrorCheck.checkResponse("跟进动态数据:", response);
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
                public void failure(final RetrofitError error) {
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
    public void onClick(final View v) {
        switch (v.getId()) {

            /*返回*/
            case R.id.img_title_left:
                onBackPressed();
                break;

            /*新建*/
            case R.id.layout_add:
              /*  Bundle bundle = new Bundle();
                bundle.putSerializable(Customer.class.getName(), customer);
                app.startActivityForResult(this, SaleActivitiesAddActivity.class, MainApp.ENTER_TYPE_RIGHT, ACTIVITIES_ADD, bundle);*/

                Bundle bundle = new Bundle();
                bundle.putSerializable(Customer.class.getName(), customer);
                app.startActivityForResult(this, DynamicAddActivity.class, MainApp.ENTER_TYPE_RIGHT, ACTIVITIES_ADD, bundle);
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
            /*新建跟进动态回调*/
            case ACTIVITIES_ADD:
                getData();
                break;

            default:
                break;
        }
    }

    @Override
    public void onPullDownToRefresh(final PullToRefreshBase refreshView) {
        isTopAdd = true;
        paginationX.setPageIndex(1);
        getData();
    }

    @Override
    public void onPullUpToRefresh(final PullToRefreshBase refreshView) {
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
        public Object getItem(final int i) {
            return lstData_saleActivity_current.isEmpty() ? null : lstData_saleActivity_current.get(i);
        }

        @Override
        public long getItemId(final int i) {
            return i;
        }

        @Override
        public View getView(final int i, View convertView, final ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_saleactivities_group_child, null);
            }

            ListView lv_listview    = ViewHolder.get(convertView,R.id.lv_listview);
            TextView tv_create_time = ViewHolder.get(convertView, R.id.tv_create_time);
            TextView tv_content = ViewHolder.get(convertView, R.id.tv_content);
            TextView tv_contact_name = ViewHolder.get(convertView, R.id.tv_contact_name);
            TextView tv_follow_name = ViewHolder.get(convertView, R.id.tv_follow_name);
            TextView tv_time = ViewHolder.get(convertView, R.id.tv_time);
            ImageView iv_imgTime = ViewHolder.get(convertView, R.id.iv_imgTime);
            SaleActivity saleActivity = lstData_saleActivity_current.get(i);

            tv_create_time.setText(DateTool.getDiffTime(saleActivity.getCreateAt() * 1000));
            tv_content.setText(saleActivity.getContent());
            tv_contact_name.setText("联系人：" + saleActivity.contactName);
            tv_follow_name.setText("跟进人：" + saleActivity.creatorName + " #" + saleActivity.typeName);

            try{
                nestionListAdapter = new DynamicListnestingAdapter(saleActivity.getAttachments(),mContext);
                lv_listview.setAdapter(nestionListAdapter);
            }catch (NullPointerException e){
                e.printStackTrace();
            }

            if (saleActivity.getRemindAt() != 0) {
                tv_time.setText(app.df3.format(new Date(saleActivity.getRemindAt() * 1000)));
            } else {
                tv_time.setText("无");
            }
            //提醒时间没有过当前时间变红色
            if (saleActivity.getRemindAt() > System.currentTimeMillis() / 1000) {
                tv_time.setTextColor(getResources().getColor(R.color.red1));
                iv_imgTime.setImageResource(R.drawable.icon_tx2);
            } else {
                tv_time.setTextColor(getResources().getColor(R.color.text99));
                iv_imgTime.setImageResource(R.drawable.icon_tx1);
            }
            if (i == lstData_saleActivity_current.size() - 1) {
                convertView.setBackgroundResource(R.drawable.item_bg_buttom);
            } else {
                convertView.setBackgroundColor(getResources().getColor(R.color.white));
            }
            return convertView;
        }
    }
}
