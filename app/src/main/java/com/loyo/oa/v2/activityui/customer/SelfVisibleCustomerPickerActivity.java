package com.loyo.oa.v2.activityui.customer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.library.module.widget.loading.LoadingLayout;
import com.loyo.oa.common.utils.DateTool;
import com.loyo.oa.pulltorefresh.PullToRefreshBase;
import com.loyo.oa.pulltorefresh.PullToRefreshListView;
import com.loyo.oa.v2.R;
import com.loyo.oa.v2.activityui.customer.model.Customer;
import com.loyo.oa.v2.application.MainApp;
import com.loyo.oa.v2.beans.PaginationX;
import com.loyo.oa.v2.customermanagement.api.CustomerService;
import com.loyo.oa.v2.network.DefaultLoyoSubscriber;
import com.loyo.oa.v2.tool.BaseLoadingActivity;
import com.loyo.oa.v2.tool.Utils;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * 新建拜访 [搜索客户选择]
 * Created by yyy on 15/12/24.
 *
 * Copy From SigninSelectCustomerSearch & Refactor by Ethan 2016-01-12
 */
public class SelfVisibleCustomerPickerActivity extends BaseLoadingActivity implements PullToRefreshListView.OnRefreshListener2, View.OnClickListener {

    public final static String KEY_CAN_RETURN_EMPTY = "com.loyo.SelfVisibleCustomerPickerActivity.KEY_CAN_RETURN_EMPTY";

    protected String strSearch;
    protected View headerView;
    public RelativeLayout headerViewBtn;
    protected EditText edt_search;
    protected PullToRefreshListView expandableListView_search;
    protected ArrayList<Customer> lstData = new ArrayList<>();
    protected CommonSearchAdapter adapter;
    private Context mContext;
    private boolean isTopAdd = true;
    private int page = 1;
    private boolean canReturnEmpty = true;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        retrieveData();
        initView();
    }

    @Override
    public void setLayoutView() {
        setContentView(R.layout.activity_public_search);
    }

    @Override
    public void getPageData() {
        ll_loading.setStatus(LoadingLayout.Loading);
        doSearch();
    }

    public void initView() {
        LayoutInflater mInflater = LayoutInflater.from(this);
        headerView = mInflater.inflate(R.layout.item_baseserach_null, null);
        headerViewBtn = (RelativeLayout) headerView.findViewById(R.id.item_baseserach_btn);
        headerViewBtn.setVisibility(canReturnEmpty?View.VISIBLE:View.GONE);
        headerViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                app.finishActivity(SelfVisibleCustomerPickerActivity.this,
                        MainApp.ENTER_TYPE_LEFT, RESULT_OK, new Intent());
            }
        });

        findViewById(R.id.img_title_left).setOnClickListener(this);
        findViewById(R.id.iv_clean).setOnClickListener(this);
        edt_search = (EditText) findViewById(R.id.edt_search);
        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(final CharSequence charSequence, final int i, final int i1, final int i2) {

            }

            @Override
            public void onTextChanged(final CharSequence charSequence, final int i, final int i1, final int i2) {

            }

            @Override
            public void afterTextChanged(final Editable editable) {
                doSearch();
            }
        });
        edt_search.requestFocus();
        expandableListView_search = (PullToRefreshListView) findViewById(R.id.expandableListView_search);
        expandableListView_search.setMode(PullToRefreshBase.Mode.BOTH);
        expandableListView_search.setOnRefreshListener(this);
        expandableListView_search.getRefreshableView().addHeaderView(headerView);
        adapter = new CommonSearchAdapter();
        expandableListView_search.setAdapter(adapter);
        /**列表监听器*/
        expandableListView_search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> adapterView, final View view, final int position, final long l) {
                returnData(position - 1);
            }
        });

        ll_loading.setStatus(LoadingLayout.Loading);
        dataRequestvoid();
    }

    private void retrieveData() {
        Intent intent = getIntent();
        if (intent != null) {
            canReturnEmpty = intent.getBooleanExtra(KEY_CAN_RETURN_EMPTY ,true);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_title_left:
                onBackPressed();
                break;
            case R.id.iv_clean:
                edt_search.setText("");
                break;
        }
    }

    /**
     * 请求体
     */
    void dataRequestvoid() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("pageIndex", page);
        params.put("pageSize", 100);
        params.put("keyWords", edt_search.getText().toString().trim());

        CustomerService.getInvovedCustomers(params)
                .subscribe(new DefaultLoyoSubscriber<PaginationX<Customer>>(ll_loading) {
            @Override
            public void onNext(PaginationX<Customer> customerPaginationX) {
                ll_loading.setStatus(LoadingLayout.Success);
                expandableListView_search.onRefreshComplete();
                if (null == customerPaginationX) {
                    if (isTopAdd) {
                        showNoData();
                    } else {
                        Toast("没有更多数据!");
                    }
                    return;
                }
                ArrayList<Customer> lstDataTemp = customerPaginationX.getRecords();
                if (lstDataTemp == null || lstDataTemp.size() == 0) {
                    if (isTopAdd) {
                        showNoData();
                    } else {
                        Toast("没有更多数据!");
                    }
                    return;
                } else {
                    if (isTopAdd) {
                        lstData.clear();
                    }
                    lstData.addAll(lstDataTemp);
                }
                changeAdapter();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                expandableListView_search.onRefreshComplete();
            }
        });
    }

    void doSearch() {
        onPullDownToRefresh(expandableListView_search);
    }


    @Override
    public void onPullDownToRefresh(final PullToRefreshBase refreshView) {
        isTopAdd = true;
        page = 1;
        dataRequestvoid();
    }

    @Override
    public void onPullUpToRefresh(final PullToRefreshBase refreshView) {
        isTopAdd = false;
        page++;
        dataRequestvoid();
    }

    void showNoData() {
        ll_loading.setStatus(LoadingLayout.Empty);
    }


    protected void returnData(final int position) {
        Intent intent = new Intent();
        Customer cus = new Customer();
        cus.id = lstData.get(position).id;
        cus.name = lstData.get(position).name;
        cus.contacts = lstData.get(position).contacts;
        intent.putExtra("data", cus);

        setResult(RESULT_OK, intent);
        onBackPressed();
    }


    public void changeAdapter() {
        adapter.notifyDataSetChanged();
    }


    public class CommonSearchAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return lstData.size();
        }

        @Override
        public Object getItem(final int i) {
            return lstData.get(i);
        }

        @Override
        public long getItemId(final int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder;
            if (convertView == null) {
                holder = new Holder();
                convertView = LayoutInflater.from(SelfVisibleCustomerPickerActivity.this)
                        .inflate(R.layout.item_visible_customer_picker_cell, null);
                holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                holder.tv_followup_time = (TextView) convertView.findViewById(R.id.tv_followup_time);
                holder.tv_tags = (TextView) convertView.findViewById(R.id.tv_tags);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            holder.setContent(lstData.get(position));
            return convertView;
        }

        class Holder {
            TextView tv_name, tv_followup_time, tv_tags;

            public void setContent(Customer item) {
                tv_name.setText(item.name);
                String lastActivityAt = DateTool.getDateTimeFriendly(item.lastActAt);
                tv_followup_time.setText("跟进时间：" + lastActivityAt);

                String tagItems = Utils.getTagItems(item);
                tv_tags.setText("标签：" + tagItems);
            }
        }
    }
}
